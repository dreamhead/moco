package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpRequest;
import com.google.common.base.Objects;

public class HttpRequestDelegate {
    private final HttpRequest request;

    public HttpRequestDelegate(HttpRequest request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper("HTTP Request")
                .omitNullValues()
                .add("uri", request.getUri())
                .add("version", request.getVersion())
                .add("method", request.getMethod())
                .add("queries", request.getQueries())
                .add("headers", request.getHeaders())
                .add("content", request.getContent())
                .toString();
    }
}
