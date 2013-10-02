package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.HttpRequest;
import com.google.common.base.Objects;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DumpHttpRequest extends Message implements HttpRequest {
    private Map<String, String> queries = newHashMap();
    private String method;
    private String uri;

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String getUri() {
        return uri;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), queries, method, uri);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(DumpHttpRequest.class)
                .omitNullValues()
                .add("uri", uri)
                .add("version", version)
                .add("queries", queries)
                .add("method", method)
                .add("headers", headers)
                .add("content", content)
                .toString();
    }
}
