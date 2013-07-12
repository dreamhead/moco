package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class UriResource implements Resource {
    private final String uri;

    public UriResource(String uri) {
        this.uri = uri;
    }

    @Override
    public String id() {
        return "uri";
    }

    @Override
    public byte[] readFor(HttpRequest request) {
        return this.uri.getBytes();
    }

    @Override
    public Resource apply(final MocoConfig config) {
        if (config.isFor(this.id())) {
            return new UriResource(config.apply(this.uri));
        }

        return this;
    }
}
