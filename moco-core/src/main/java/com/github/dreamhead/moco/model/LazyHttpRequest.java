package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.extractor.FormsRequestExtractor;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

public class LazyHttpRequest implements HttpRequest {
    private final FullHttpRequest request;
    private final Supplier<ImmutableMap<String, String>> headersSupplier;
    private final Supplier<ImmutableMap<String,String>> queriesSupplier;
    private final Supplier<String> contentSupplier;
    private final Supplier<ImmutableMap<String, String>> formSupplier;

    public LazyHttpRequest(FullHttpRequest request) {
        this.request = request;
        this.queriesSupplier = queriesSupplier(request.getUri());
        this.headersSupplier = headersSupplier(request.headers());
        this.contentSupplier = contentSupplier(request);
        this.formSupplier = formSupplier(request);
    }

    @Override
    public String getUri() {
        return this.request.getUri();
    }

    @Override
    public ImmutableMap<String, String> getQueries() {
        return this.queriesSupplier.get();
    }

    @Override
    public String getMethod() {
        return request.getMethod().name();
    }

    @Override
    public String getVersion() {
        return request.getProtocolVersion().text();
    }

    @Override
    public String getContent() {
        return contentSupplier.get();
    }

    @Override
    public ImmutableMap<String, String> getHeaders() {
        return headersSupplier.get();
    }

    @JsonIgnore
    public ImmutableMap<String, String> getForms() {
        return formSupplier.get();
    }

    private Supplier<ImmutableMap<String, String>> queriesSupplier(final String uri) {
        return Suppliers.memoize(new Supplier<ImmutableMap<String, String>>() {
            @Override
            public ImmutableMap<String, String> get() {
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

                QueryStringDecoder decoder = new QueryStringDecoder(uri);
                for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
                    builder.put(entry.getKey(), entry.getValue().get(0));
                }
                return builder.build();
            }
        });
    }

    private Supplier<ImmutableMap<String, String>> headersSupplier(final HttpHeaders requestHeaders) {
        return Suppliers.memoize(new Supplier<ImmutableMap<String, String>>() {
            @Override
            public ImmutableMap<String, String> get() {
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                for (Map.Entry<String, String> entry : requestHeaders) {
                    builder.put(entry.getKey(), entry.getValue());
                }
                return builder.build();
            }
        });
    }

    private Supplier<String> contentSupplier(final FullHttpRequest request) {
        return Suppliers.memoize(new Supplier<String>() {
            @Override
            public String get() {
                String text = request.content().toString(Charset.defaultCharset());
                return isNullOrEmpty(text) ? null : text;
            }
        });
    }

    private Supplier<ImmutableMap<String, String>> formSupplier(final FullHttpRequest request) {
        return Suppliers.memoize(new Supplier<ImmutableMap<String, String>>() {
            @Override
            public ImmutableMap<String, String> get() {
                return new FormsRequestExtractor().extract(request).get();
            }
        });
    }

    @Override
    public String toString() {
        return new HttpRequestDelegate(this).toString();
    }
}
