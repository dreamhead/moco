package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpRequest;
import com.google.common.base.Objects;

public abstract class AbstractHttpRequest implements HttpRequest {
    @Override
    public String toString() {
        return Objects.toStringHelper("HTTP Request")
                .omitNullValues()
                .add("uri", getUri())
                .add("version", getVersion())
                .add("method", getMethod())
                .add("queries", getQueries())
                .add("headers", getHeaders())
                .add("content", getContent())
                .toString();
    }
}
