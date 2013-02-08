package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Objects;

import java.util.Map;

public class ResponseSetting extends AbstractResource {
    protected String status;
    protected Map<String, String> headers;
    private CacheSetting cache;

    public String getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public CacheSetting getCache() {
        return cache;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("text", text)
                .add("file", file)
                .add("status", status)
                .add("headers", headers)
                .add("url", url)
                .add("cache", cache)
                .toString();
    }

    public boolean isResource() {
        return (text != null)
                || (file != null)
                || (url != null)
                || (cache != null);
    }

    @Override
    public Resource retrieveResource() {
        Resource resource = super.retrieveResource();
        if (resource != null) {
            return resource;
        }
        Resource cacheResource = cache.retrieveResource();
        if (cacheResource != null) {
            return cacheResource;
        }

        throw new IllegalArgumentException("unknown response setting with " + this);
    }
}
