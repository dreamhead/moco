package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class ContentHandler extends AbstractContentHandler {
    private final ContentResource resource;

    public ContentHandler(final ContentResource resource) {
        this.resource = resource;
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        buffer.writeBytes(resource.asByteArray());
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
