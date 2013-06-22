package com.github.dreamhead.moco.handler.failover;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.base.Objects;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Request extends Message {
    private Map<String, String> queries;
    private String method;

    public void addQuery(String key, String value) {
        this.queries.put(key, value);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean match(Request that) {
        return super.match(that) && doMatch(method, that.method)
                && doMatch(queries, that.queries);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), queries, method);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(Request.class)
                .add("version", version)
                .add("queries", queries)
                .add("method", method)
                .add("headers", headers)
                .add("content", content)
                .toString();
    }
}
