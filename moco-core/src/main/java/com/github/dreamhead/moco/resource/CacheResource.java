package com.github.dreamhead.moco.resource;

public class CacheResource implements Resource {
    private Resource resource;
    private byte[] cache;

    public CacheResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String id() {
        return resource.id();
    }

    @Override
    public byte[] asByteArray() {
        if (cache == null) {
            cache = resource.asByteArray();
        }

        return cache;
    }
}
