package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.net.MediaType;

import static com.google.common.base.Optional.of;

public class ContentHandler extends AbstractContentResponseHandler {
    private final ContentResource resource;

    public ContentHandler(final ContentResource resource) {
        this.resource = resource;
    }

    @Override
    protected MessageContent responseContent(final Request request) {
        return this.resource.readFor(of(request));
    }

    @Override
    protected MediaType getContentType(final HttpRequest request) {
        return resource.getContentType(request);
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        ResponseHandler handler = super.apply(config);
        if (handler != this) {
            return handler;
        }

        Resource appliedReosurce = this.resource.apply(config);
        if (appliedReosurce != this.resource) {
            return new ContentHandler((ContentResource) appliedReosurce);
        }

        return this;
    }
}
