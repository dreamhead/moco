package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.google.common.collect.ImmutableMap;

public class DefaultMutableHttpResponse implements MutableHttpResponse {
    private HttpProtocolVersion version;
    private ImmutableMap<String, String> headers;
    private int status;
    private String content;

    private DefaultMutableHttpResponse() {
    }

    @Override
    public void setVersion(HttpProtocolVersion version) {
        this.version = version;
    }

    @Override
    public void setHeaders(ImmutableMap<String, String> headers) {
        this.headers = headers;
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
    public HttpProtocolVersion getVersion() {
        return this.version;
    }

    @Override
    public ImmutableMap<String, String> getHeaders() {
        return this.headers;
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
