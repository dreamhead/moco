package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class SessionContext {
    private FullHttpResponse response;
    private HttpRequest httpRequest;

    public SessionContext(HttpRequest httpRequest, FullHttpResponse response) {
        this.httpRequest = httpRequest;
        this.response = response;
    }

    public HttpRequest getRequest() {
        return this.httpRequest;
    }

    public FullHttpResponse getResponse() {
        return response;
    }
}
