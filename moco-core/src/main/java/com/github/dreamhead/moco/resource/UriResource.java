package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

public class UriResource implements Resource {
    private String uri;

    public UriResource(String uri) {
        this.uri = uri;
    }

    @Override
    public String id() {
        return "uri";
    }

    @Override
    public byte[] asByteArray() {
        return this.uri.getBytes();
    }

    @Override
    public void apply(MocoConfig config) {
        if (config.isFor(this.id())) {
            this.uri = config.apply(this.uri);
        }
    }
}
