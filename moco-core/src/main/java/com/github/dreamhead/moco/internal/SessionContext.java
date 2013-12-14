package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class SessionContext {
    private FullHttpRequest request;
    private FullHttpResponse response;
    private HttpRequest httpRequest;

    public SessionContext(HttpRequest httpRequest, FullHttpRequest request, FullHttpResponse response) {
        this.httpRequest = httpRequest;
        this.request = request;
        this.response = response;
    }

    public HttpRequest getRequest() {
        return this.httpRequest;
    }

    public FullHttpRequest getFullHttpRequest() {
        return request;
    }

    public FullHttpResponse getResponse() {
        return response;
    }
}
