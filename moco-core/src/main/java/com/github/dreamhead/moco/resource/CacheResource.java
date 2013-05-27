package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

public class CacheResource implements ContentResource {
    private final ContentResource resource;
    private LocalCache localCache;
    private byte[] cache;

    public CacheResource(ContentResource resource, LocalCache localCache) {
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

    @Override
    public Resource apply(final MocoConfig config) {
        return new CacheResource((ContentResource)resource.apply(config), localCache.apply(config));
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

    @Override
    public String getContentType() {
        return resource.getContentType();
    }
}
