package com.github.dreamhead.moco.model;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

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
    public void addHeader(String name, Object value) {
        this.headers.put(name, value.toString());
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

    public FullHttpResponse toFullResponse() {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.valueOf(this.version.text()), HttpResponseStatus.valueOf(this.status));
        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            response.headers().add(entry.getKey(), entry.getValue());
        }

        response.content().writeBytes(this.content.getBytes());
        return response;
    }
}
