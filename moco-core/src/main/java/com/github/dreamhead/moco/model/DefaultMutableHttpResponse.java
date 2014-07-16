package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class DefaultMutableHttpResponse implements MutableHttpResponse {
    private HttpProtocolVersion version;
    private Map<String, String> headers = Maps.newHashMap();
    private int status;
    private String content;

    private DefaultMutableHttpResponse() {
    }

    @Override
    public void setVersion(HttpProtocolVersion version) {
        this.version = version;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    @Override
    public HttpProtocolVersion getVersion() {
        return this.version;
    }

    @Override
    public ImmutableMap<String, String> getHeaders() {
        return ImmutableMap.copyOf(this.headers);
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    public static MutableHttpResponse newResponse() {
        return new DefaultMutableHttpResponse();
    }
}
