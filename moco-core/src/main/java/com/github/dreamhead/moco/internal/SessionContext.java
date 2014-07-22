package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;

public class SessionContext {
    private HttpRequest httpRequest;
    private MutableHttpResponse httpResponse;

    public SessionContext(HttpRequest httpRequest, MutableHttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public HttpRequest getRequest() {
        return this.httpRequest;
    }

    public MutableHttpResponse getHttpResponse() {
        return httpResponse;
    }
}
