package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;

import static com.google.common.base.Optional.of;

public class ContentHandler extends AbstractContentResponseHandler {
    private final ContentResource resource;

    public ContentHandler(final ContentResource resource) {
        this.resource = resource;
    }

    @Override
    protected String responseContent(final HttpRequest request) {
        return new String(this.resource.readFor(of(request)));
    }

    @Override
    protected String getContentType(final HttpRequest request) {
        return resource.getContentType();
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        ResponseHandler handler = super.apply(config);
        if (handler != this) {
            return handler;
        }

        Resource resource = this.resource.apply(config);
        if (resource != this.resource) {
            return new ContentHandler((ContentResource) resource);
        }

        return this;
    }
}
