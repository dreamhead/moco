package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpRequest;

public class ContentHandler extends AbstractContentResponseHandler {
    private final ContentResource resource;

    public ContentHandler(final ContentResource resource) {
        this.resource = resource;
    }

    @Override
    protected void writeContentResponse(HttpRequest request, ByteBuf buffer) {
        buffer.writeBytes(this.resource.readFor(request));
    }

    @Override
    protected String getContentType(HttpRequest request) {
        return resource.getContentType();
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return new ContentHandler((ContentResource)this.resource.apply(config));
    }
}
