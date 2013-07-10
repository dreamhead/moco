package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class VersionResource implements Resource {
    private final Resource version;

    public VersionResource(Resource version) {
        this.version = version;
    }

    @Override
    public String id() {
        return "version";
    }

    @Override
    public byte[] asByteArray(HttpRequest request) {
        return version.asByteArray(request);
    }

    @Override
    public Resource apply(final MocoConfig config) {
        return this;
    }
}
