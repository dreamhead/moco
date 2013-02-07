package com.github.dreamhead.moco.resource;

public class CacheResource implements Resource {
    private Resource resource;
    private LocalCache localCache;
    private byte[] cache;

    public CacheResource(Resource resource, LocalCache localCache) {
        this.resource = resource;
        this.localCache = localCache;
    }

    @Override
    public String id() {
        return resource.id();
    }

    @Override
    public byte[] asByteArray() {
        if (cache == null) {
            cache = content();
        }

        return cache;
    }

    private byte[] content() {
        try {
            byte[] content = resource.asByteArray();
            localCache.write(content);
            return content;
        } catch (RuntimeException e) {
            return contentFromLocalCache();
        }
    }

    private byte[] contentFromLocalCache() {
        return localCache.read();
    }
}
