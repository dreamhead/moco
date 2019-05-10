package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.net.MediaType;

public class ContentHandler extends AbstractContentResponseHandler {
    private final ContentResource resource;

    public ContentHandler(final ContentResource resource) {
        this.resource = resource;
    }

    public final ContentResource getResource() {
        return resource;
    }

    @Override
    protected final MessageContent responseContent(final Request request) {
        return this.resource.readFor(request);
    }

    @Override
    protected final MediaType getContentType(final HttpRequest request) {
        return resource.getContentType(request);
    }

    @Override
    public final ResponseHandler apply(final MocoConfig config) {
        ResponseHandler handler = super.apply(config);
        if (handler != this) {
            return handler;
        }

        Resource appliedResource = this.resource.apply(config);
        if (appliedResource != this.resource) {
            return new ContentHandler((ContentResource) appliedResource);
        }

        return this;
    }
}
