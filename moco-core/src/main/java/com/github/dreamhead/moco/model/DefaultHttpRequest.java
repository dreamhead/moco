package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.extractor.CookiesRequestExtractor;
import com.github.dreamhead.moco.extractor.FormsRequestExtractor;
import com.github.dreamhead.moco.internal.Client;
import com.github.dreamhead.moco.util.Suppliers;
import com.github.dreamhead.moco.util.ToStringHelper;
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
import static com.github.dreamhead.moco.util.Maps.orderedCopyOf;
import static com.github.dreamhead.moco.util.Maps.toOrderedMap;

@JsonDeserialize(builder = DefaultHttpRequest.Builder.class)
public final class DefaultHttpRequest extends DefaultHttpMessage implements HttpRequest {
    private final Supplier<Map<String, String>> formSupplier;
    private final Supplier<Map<String, String>> cookieSupplier;

    private final HttpMethod method;

    private final String uri;
    private final Map<String, String[]> queries;

    @JsonIgnore
    private final Client client;

    private DefaultHttpRequest(final HttpProtocolVersion version, final MessageContent content,
                               final HttpMethod method, final String uri,
                               final Map<String, String[]> headers,
                               final Map<String, String[]> queries,
                               final Client client) {
        super(version, content, headers);
        this.method = method;
        this.uri = uri;
        this.queries = queries;
        this.client = client;
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
    public Map<String, String> getForms() {
        return formSupplier.get();
    }

    @JsonIgnore
    public Map<String, String> getCookies() {
        return cookieSupplier.get();
    }

    @Override
    @JsonSerialize(as = Map.class)
    public Map<String, String[]> getQueries() {
        return queries;
    }

    private Supplier<Map<String, String>> formSupplier() {
        return Suppliers.memoize(() -> {
            Optional<Map<String, String>> forms =
                    new FormsRequestExtractor().extract(DefaultHttpRequest.this);
            return forms.orElseGet(Map::of);
        });
    }

    private Supplier<Map<String, String>> cookieSupplier() {
        return Suppliers.memoize(() -> {
            Optional<Map<String, String>> cookies =
                    new CookiesRequestExtractor().extract(DefaultHttpRequest.this);
            return cookies.orElseGet(Map::of);
        });
    }

    @Override
    public Client getClient() {
        return client;
    }

    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("uri", this.uri)
                .add("method", this.method)
                .add("queries", this.queries)
                .add("client", this.client);
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

    public static HttpRequest newRequest(final FullHttpRequest request, final Client client) {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, String[]> queries = toQueries(decoder);

        return builder()
                .withVersion(HttpProtocolVersion.versionOf(request.protocolVersion().text()))
                .withHeaders(toHeaders(request))
                .withMethod(HttpMethod.valueOf(request.method().toString().toUpperCase()))
                .withUri(decoder.path())
                .withQueries(queries)
                .withContent(toMessageContent(request))
                .withClient(client)
                .build();
    }

    private static Map<String, String[]> toQueries(final QueryStringDecoder decoder) {
        return decoder.parameters().entrySet().stream()
                .collect(toOrderedMap(Map.Entry::getKey, entry -> entry.getValue().toArray(new String[0])));
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
        private Map<String, String[]> queries;
        private Client client;

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
                this.queries = orderedCopyOf(queries);
            }

            return this;
        }

        public Builder withClient(final Client client) {
            if (client != null) {
                this.client = client;
            }

            return this;
        }

        public DefaultHttpRequest build() {
            return new DefaultHttpRequest(this.getVersion(), this.getContent(), method,
                    this.uri, this.getHeaders(), this.queries, this.client);
        }
    }
}
