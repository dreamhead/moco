package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.recorder.MocoGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class SessionContext {
    private final Request request;
    private final Response response;
    private final SessionGroup group;
    private final Map<ContextKey, Object> context = new HashMap<>();

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

    public void writeAndFlush(final Object message, final MocoGroup group) {
        Objects.requireNonNull(this.group).writeAndFlush(message, group);
    }

    public void join(final MocoGroup group) {
        Objects.requireNonNull(this.group).join(group);
    }

    public <T> void register(final ContextKey key, final T value) {
        this.context.put(key, value);
    }

    public <T> T get(final ContextKey key, final Class<T> clazz) {
        return clazz.cast(this.context.get(key));
    }
}
