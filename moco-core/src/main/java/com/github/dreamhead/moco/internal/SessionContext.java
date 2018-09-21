package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

public final class SessionContext {
    private final Request request;
    private final Response response;

    public SessionContext(final Request request, final Response response) {
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return this.request;
    }

    public Response getResponse() {
        return response;
    }
}
