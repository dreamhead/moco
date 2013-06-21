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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Message)) {
            return false;
        }


        Request that = (Request) obj;
        return super.equals(that) && doEquals(method, that.method)
                && doEquals(queries, that.queries);
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
                .add("header", headers)
                .add("content", content)
                .toString();
    }
}
