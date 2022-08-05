package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.extractor.CookiesRequestExtractor;
import com.github.dreamhead.moco.extractor.FormsRequestExtractor;
import com.github.dreamhead.moco.util.Suppliers;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.QueryStringEncoder;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.github.dreamhead.moco.model.MessageContent.content;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

@JsonDeserialize(builder = DefaultHttpRequest.Builder.class)
public final class DefaultHttpRequest extends DefaultHttpMessage implements HttpRequest {
    private final Supplier<ImmutableMap<String, String>> formSupplier;
    private final Supplier<ImmutableMap<String, String>> cookieSupplier;

    private final HttpMethod method;

    private final String uri;
    private final ImmutableMap<String, String[]> queries;
    private final String clientAddress;

    private DefaultHttpRequest(final HttpProtocolVersion version, final MessageContent content,
                               final HttpMethod method, final String uri,
                               final ImmutableMap<String, String[]> headers,
                               final ImmutableMap<String, String[]> queries,
                               final String clientAddress) {
        super(version, content, headers);
        this.method = method;
        this.uri = uri;
        this.queries = queries;
        this.clientAddress = clientAddress;
        this.formSupplier = formSupplier();
        this.cookieSupplier = cookieSupplier();
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @JsonIgnore
    public ImmutableMap<String, String> getForms() {
        return formSupplier.get();
    }

    @JsonIgnore
    public ImmutableMap<String, String> getCookies() {
        return cookieSupplier.get();
    }

    @Override
    @JsonSerialize(as = Map.class)
    public ImmutableMap<String, String[]> getQueries() {
        return queries;
    }

    private Supplier<ImmutableMap<String, String>> formSupplier() {
        return Suppliers.memoize(() -> {
            Optional<ImmutableMap<String, String>> forms =
                    new FormsRequestExtractor().extract(DefaultHttpRequest.this);
            return forms.orElseGet(ImmutableMap::of);
        });
    }

    private Supplier<ImmutableMap<String, String>> cookieSupplier() {
        return Suppliers.memoize(() -> {
            Optional<ImmutableMap<String, String>> cookies =
                    new CookiesRequestExtractor().extract(DefaultHttpRequest.this);
            return cookies.orElseGet(ImmutableMap::of);
        });
    }

    @Override
    public String getClientAddress() {
        return clientAddress;
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("uri", this.uri)
                .add("method", this.method)
                .add("queries", this.queries)
                .add("clientAddress", this.clientAddress);
    }

    public static Builder builder() {
        return new Builder();
    }

    private static MessageContent toMessageContent(final FullHttpRequest request) {
        long contentLength = HttpUtil.getContentLength(request, -1);
        if (contentLength <= 0) {
            return content().build();
        }

        return content()
                .withCharset(HttpUtil.getCharset(request))
                .withContent(new ByteBufInputStream(request.content()))
                .build();
    }

    public static HttpRequest newRequest(final FullHttpRequest request, final String clientAddress) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        ImmutableMap<String, String[]> queries = toQueries(decoder);

        return builder()
                .withVersion(HttpProtocolVersion.versionOf(request.protocolVersion().text()))
                .withHeaders(toHeaders(request))
                .withMethod(HttpMethod.valueOf(request.method().toString().toUpperCase()))
                .withUri(decoder.path())
                .withQueries(queries)
                .withContent(toMessageContent(request))
                .withClientAddress(clientAddress)
                .build();
    }

    private static ImmutableMap<String, String[]> toQueries(final QueryStringDecoder decoder) {
        return decoder.parameters().entrySet().stream()
                .collect(toImmutableMap(Map.Entry::getKey, entry -> entry.getValue().toArray(new String[0])));
    }

    public FullHttpRequest toFullHttpRequest() {
        ByteBuf buffer = Unpooled.buffer();
        MessageContent content = getContent();
        if (content != null) {
            buffer.writeBytes(content.getContent());
        }

        QueryStringEncoder encoder = new QueryStringEncoder(uri);
        for (Map.Entry<String, String[]> entry : queries.entrySet()) {
            String[] values = entry.getValue();
            for (String value : values) {
                encoder.addParam(entry.getKey(), value);
            }
        }

        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.valueOf(getVersion().text()),
                io.netty.handler.codec.http.HttpMethod.valueOf(method.name()), encoder.toString(), buffer);

        for (Map.Entry<String, String[]> entry : getHeaders().entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                request.headers().add(key, value);
            }
        }

        return request;
    }

    public static final class Builder extends DefaultHttpMessage.Builder<Builder> {
        private HttpMethod method;
        private String uri;
        private ImmutableMap<String, String[]> queries;
        private String clientAddress;

        public Builder withMethod(final HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder withUri(final String uri) {
            this.uri = uri;
            return this;
        }

        public Builder withQueries(final Map<String, String[]> queries) {
            if (queries != null) {
                this.queries = copyOf(queries);
            }

            return this;
        }

        public Builder withClientAddress(final String clientAddress) {
            if (clientAddress != null) {
                this.clientAddress = clientAddress;
            }

            return this;
        }

        public DefaultHttpRequest build() {
            return new DefaultHttpRequest(this.getVersion(), this.getContent(), method,
                    this.uri, this.getHeaders(), this.queries, this.clientAddress);
        }
    }
}
