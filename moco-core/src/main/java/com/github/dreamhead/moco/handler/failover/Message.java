package com.github.dreamhead.moco.handler.failover;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class Message {
    private String version;
    private String content;
    private Map<String, String> headers = newHashMap();

    public void setVersion(String version) {
        this.version = version;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getVersion() {
        return version;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
