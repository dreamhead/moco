package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.resource.ContentResource;

public final class JsonResponseHandler extends ContentHandler {
    public JsonResponseHandler(final ContentResource resource) {
        super(resource);
    }

    public Object getPojo() {
        return getResource().getJsonObject().get();
    }
}
