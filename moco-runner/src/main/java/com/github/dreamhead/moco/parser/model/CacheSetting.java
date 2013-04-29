package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Objects;

import static com.github.dreamhead.moco.MocoCache.cache;
import static com.github.dreamhead.moco.MocoCache.with;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class CacheSetting extends AbstractResource {
    private FileSetting with;

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .add("text", text)
                .add("file", file)
                .add("url", url)
                .add("with", with)
                .toString();
    }

    @Override
    public Resource retrieveResource() {
        Resource resource = super.retrieveResource();
        if (resource == null && !ContentResource.class.isInstance(resource)) {
            throw new IllegalArgumentException("unknown response setting with " + this);
        }

        return initializeResource(ContentResource.class.cast(resource));
    }

    private Resource initializeResource(ContentResource contentResource) {
        if (with != null) {
            return cache(contentResource, with(with.retrieveResource()));
        }

        return cache(contentResource);
    }
}
