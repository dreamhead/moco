package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

public class SessionContext {
    private FullHttpResponse response;
    private HttpRequest httpRequest;
    private MutableHttpResponse httpResponse;

    public SessionContext(HttpRequest httpRequest, FullHttpResponse response, MutableHttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.response = response;
        this.httpResponse = httpResponse;
    }

    public HttpRequest getRequest() {
        return this.httpRequest;
    }

    public FullHttpResponse getResponse() {
        return response;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }
}
