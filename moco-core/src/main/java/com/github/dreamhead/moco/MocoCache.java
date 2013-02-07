package com.github.dreamhead.moco;

import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.resource.LocalCache;
import com.github.dreamhead.moco.resource.CacheResource;
import com.github.dreamhead.moco.resource.WritableResource;
import com.github.dreamhead.moco.resource.DefaultLocalCache;

import static com.google.common.base.Preconditions.checkNotNull;

public class MocoCache {
    public static Resource cache(Resource resource) {
        return cache(resource, LocalCache.EMPTY_LOCAL_CACHE);
    }

    public static Resource cache(Resource resource, LocalCache localCache) {
        checkNotNull(resource, "Null resource is not allowed for cache");
        checkNotNull(localCache, "Null local cache is not allowed for cache");

        return new CacheResource(resource, localCache);
    }

    public static LocalCache with(WritableResource resource) {
        return new DefaultLocalCache(resource);
    }
}
