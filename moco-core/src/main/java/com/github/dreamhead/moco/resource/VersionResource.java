package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

public class VersionResource implements Resource {
    private final String version;

    public VersionResource(String version) {
        this.version = version;
    }

    @Override
    public String id() {
        return "version";
    }

    @Override
    public byte[] asByteArray() {
        return version.getBytes();
    }

    @Override
    public void apply(MocoConfig config) {
    }
}
