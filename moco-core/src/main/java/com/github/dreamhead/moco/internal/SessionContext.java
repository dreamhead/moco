package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

public class SessionContext {
    private Request request;
    private Response response;

    public SessionContext(Request request, Response response) {
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
