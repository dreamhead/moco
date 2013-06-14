package com.github.dreamhead.moco.handler.failover;

import java.util.Map;

public class Request extends Message {
    private Map<String, String> queries;
    private String method;

    public Map<String, String> getQueries() {
        return queries;
    }

    public void addQuery(String key, String value) {
        this.queries.put(key, value);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
