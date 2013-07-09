package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class MethodResource implements Resource {
    private final String method;

    public MethodResource(String method) {
        this.method = method.toUpperCase();
    }

    @Override
    public String id() {
        return "method";
    }

    @Override
    public byte[] asByteArray(HttpRequest request) {
        return method.getBytes();
    }

    @Override
    public Resource apply(final MocoConfig config) {
        return this;
    }
}
