package com.github.dreamhead.moco.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Objects;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DefaultRequest extends Message implements Request {
    private Map<String, String> queries = newHashMap();
    private String method;

    public void addQuery(String key, String value) {
        this.queries.put(key, value);
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

    public boolean match(DefaultRequest that) {
        return super.match(that) && doMatch(method, that.method)
                && doMatch(queries, that.queries);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), queries, method);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(DefaultRequest.class)
                .omitNullValues()
                .add("version", version)
                .add("queries", queries)
                .add("method", method)
                .add("headers", headers)
                .add("content", content)
                .toString();
    }
}
