package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.resource.ContentResource;

public class RecorderIdentifier implements RecorderConfig {
    private ContentResource resource;

    public RecorderIdentifier(final ContentResource resource) {
        this.resource = resource;
    }

    public String getIdentifier(final HttpRequest httpRequest) {
        return this.resource.readFor(httpRequest).toString();
    }

    @Override
    public boolean isFor(final String name) {
        return IDENTIFIER.equalsIgnoreCase(name);
    }
}
