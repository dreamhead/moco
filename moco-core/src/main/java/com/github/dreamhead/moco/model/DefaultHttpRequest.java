package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.extractor.CookiesRequestExtractor;
import com.github.dreamhead.moco.extractor.FormsRequestExtractor;
import com.github.dreamhead.moco.extractor.ParamsRequestExtractor;
import com.github.dreamhead.moco.util.ByteBufs;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.nio.charset.Charset;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;

@JsonDeserialize(builder = DefaultHttpRequest.Builder.class)
public class DefaultHttpRequest implements HttpRequest {
    private final Supplier<ImmutableMap<String, String>> formSupplier;
    private final Supplier<ImmutableMap<String, String>> cookieSupplier;
    private final Supplier<ImmutableMap<String, String>> querySupplier;

    private final String version;
    private final String content;
    private final ImmutableMap<String, String> headers;
    private final String method;

    private final String uri;

    private DefaultHttpRequest(String version, String content, String method, String uri,
                            ImmutableMap<String, String> headers) {
        this.version = version;
        this.content = content;
        this.headers = headers;
        this.method = method;
        this.uri = uri;
        this.formSupplier = formSupplier();
        this.cookieSupplier = cookieSupplier();
        this.querySupplier = querySupplier();
    }

    public String getVersion() {
        return version;
    }

    public String getContent() {
        return content;
    }

    public ImmutableMap<String, String> getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

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

    @JsonIgnore
    public ImmutableMap<String, String> getQueries() {
        return querySupplier.get();
    }

    private Supplier<ImmutableMap<String, String>> querySupplier() {
        return Suppliers.memoize(new Supplier<ImmutableMap<String, String>>() {
            @Override
            public ImmutableMap<String, String> get() {
                Optional<ImmutableMap<String, String>> queries = new ParamsRequestExtractor().extract(DefaultHttpRequest.this);
                return queries.isPresent() ? queries.get() : ImmutableMap.<String, String>of();
            }
        });
    }

    private Supplier<ImmutableMap<String, String>> formSupplier() {
        return Suppliers.memoize(new Supplier<ImmutableMap<String, String>>() {
            @Override
            public ImmutableMap<String, String> get() {
                Optional<ImmutableMap<String,String>> forms = new FormsRequestExtractor().extract(DefaultHttpRequest.this);
                return forms.isPresent() ? forms.get() : ImmutableMap.<String, String>of();
            }
        });
    }

    private Supplier<ImmutableMap<String, String>> cookieSupplier() {
        return Suppliers.memoize(new Supplier<ImmutableMap<String, String>>() {
            @Override
            public ImmutableMap<String, String> get() {
                Optional<ImmutableMap<String, String>> cookies = new CookiesRequestExtractor().extract(DefaultHttpRequest.this);
                return cookies.isPresent() ? cookies.get() : ImmutableMap.<String, String>of();
            }
        });
    }

    @Override
    public String toString() {
        return Objects.toStringHelper("HTTP Request")
                .omitNullValues()
                .add("uri", this.uri)
                .add("version", this.version)
                .add("method", this.method)
                .add("headers", this.headers)
                .add("content", this.content)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static String contentToString(FullHttpRequest request) {
        long contentLength = HttpHeaders.getContentLength(request, -1);
        if (contentLength <= 0) {
            return "";
        }

        return new String(ByteBufs.asBytes(request.content()), 0, (int)contentLength, Charset.defaultCharset());
    }

    public static HttpRequest newRequest(FullHttpRequest request) {

        return builder()
                .withVersion(request.getProtocolVersion().text())
                .withHeaders(collectHeaders(request.headers()))
                .withMethod(request.getMethod().toString().toUpperCase())
                .withUri(request.getUri())
                .withContent(contentToString(request))
                .build();
    }

    public FullHttpRequest toFullHttpRequest() {
        ByteBuf buffer = Unpooled.buffer();
        if (content != null) {
            buffer.writeBytes(content.getBytes());
        }

        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.valueOf(version), HttpMethod.valueOf(method), uri, buffer);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.headers().add(entry.getKey(), entry.getValue());
        }

        return request;
    }

    private static ImmutableMap<String, String> collectHeaders(Iterable<Map.Entry<String, String>> httpHeaders) {
        ImmutableMap.Builder<String, String> headerBuilder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : httpHeaders) {
            headerBuilder.put(entry);
        }

        return headerBuilder.build();
    }

    public static final class Builder {
        private String version;
        private String content;
        private ImmutableMap<String, String> headers;
        private String method;
        private String uri;

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withHeaders(Map<String, String> headers) {
            if (headers != null) {
                this.headers = copyOf(headers);
            }

            return this;
        }

        public Builder withMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public DefaultHttpRequest build() {
            return new DefaultHttpRequest(version, content, method, uri, headers);
        }
    }
}
