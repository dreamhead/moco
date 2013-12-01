package com.github.dreamhead.moco.internal;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class SessionContext {
    private FullHttpRequest request;
    private FullHttpResponse response;

    public SessionContext(FullHttpRequest request, FullHttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public FullHttpResponse getResponse() {
        return response;
    }
}
