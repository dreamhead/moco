package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

import java.util.Objects;

public final class SessionContext {
    private final Request request;
    private final Response response;
    private final SessionGroup group;

    public SessionContext(final Request request, final Response response) {
        this.request = request;
        this.response = response;
        this.group = null;
    }

    public SessionContext(final Request request, final Response response, final SessionGroup group) {
        this.request = request;
        this.response = response;
        this.group = group;
    }

    public Request getRequest() {
        return this.request;
    }

    public Response getResponse() {
        return response;
    }

    public void writeAndFlush(final Object message) {
        Objects.requireNonNull(this.group).writeAndFlush(message);
    }
}
