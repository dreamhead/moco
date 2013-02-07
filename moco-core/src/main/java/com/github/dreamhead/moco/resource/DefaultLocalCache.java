package com.github.dreamhead.moco.resource;

public class DefaultLocalCache implements LocalCache {
    private WritableResource resource;

    public DefaultLocalCache(WritableResource resource) {
        this.resource = resource;
    }

    public void write(byte[] content) {
        resource.write(content);
    }

    public byte[] read() {
        return resource.asByteArray();
    }
}
