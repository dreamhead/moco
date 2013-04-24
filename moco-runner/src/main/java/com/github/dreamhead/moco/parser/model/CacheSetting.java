package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.base.Objects;

import static com.github.dreamhead.moco.MocoCache.cache;
import static com.github.dreamhead.moco.MocoCache.with;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CacheSetting extends AbstractResource {
    private FileSetting with;

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
    public ContentResource retrieveResource() {
        ContentResource resource = super.retrieveResource();
        if (resource == null) {
            throw new IllegalArgumentException("unknown response setting with " + this);
        }

        if (with != null) {
            return cache(resource, with(with.retrieveResource()));
        }

        return cache(resource);
    }
}
