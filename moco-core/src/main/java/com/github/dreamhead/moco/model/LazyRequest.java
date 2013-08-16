package com.github.dreamhead.moco.model;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;

public class LazyRequest implements Request {
    private final FullHttpRequest request;
    private final Supplier<Map<String, String>> headersSupplier;
    private final Supplier<Map<String,String>> queriesSupplier;
    private final Supplier<String> contentSupplier;

    public LazyRequest(FullHttpRequest request) {
        this.request = request;
        this.queriesSupplier = queriesSupplier(request.getUri());
        this.headersSupplier = headersSupplier(request.headers());
        this.contentSupplier = contentSupplier(request);
    }

    @Override
    public Map<String, String> getQueries() {
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
    public Map<String, String> getHeaders() {
        return headersSupplier.get();
    }

    private Supplier<Map<String, String>> queriesSupplier(final String uri) {
        return Suppliers.memoize(new Supplier<Map<String, String>>() {
            @Override
            public Map<String, String> get() {
                Map<String, String> queries = newHashMap();

                QueryStringDecoder decoder = new QueryStringDecoder(uri);
                for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
                    queries.put(entry.getKey(), entry.getValue().get(0));
                }
                return queries;
            }
        });
    }

    private Supplier<Map<String, String>> headersSupplier(final HttpHeaders requestHeaders) {
        return Suppliers.memoize(new Supplier<Map<String, String>>() {
            @Override
            public Map<String, String> get() {
                Map<String,String> headers = newHashMap();
                for (Map.Entry<String, String> entry : requestHeaders) {
                    headers.put(entry.getKey(), entry.getValue());
                }
                return headers;
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
}
