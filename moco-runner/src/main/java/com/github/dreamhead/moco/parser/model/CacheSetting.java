package com.github.dreamhead.moco.parser.model;

import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Objects;

import static com.github.dreamhead.moco.MocoCache.cache;
import static com.github.dreamhead.moco.MocoCache.with;

public class CacheSetting extends AbstractResource {
    private FileSetting with;

    public FileSetting getWith() {
        return with;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("text", text)
                .add("file", file)
                .add("url", url)
                .add("with", with)
                .toString();
    }

    @Override
    public Resource retrieveResource() {
        Resource resource = super.retrieveResource();
        if (resource == null) {
            throw new IllegalArgumentException("unknown response setting with " + this);
        }

        if (with != null) {
            return cache(resource, with(with.retrieveResource()));
        }

        return cache(resource);
    }
}
