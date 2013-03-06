package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.resource.Resource;
import org.jboss.netty.buffer.ChannelBuffer;

public class ContentHandler extends AbstractContentHandler {
    private final Resource resource;

    public ContentHandler(final Resource resource) {
        this.resource = resource;
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        buffer.writeBytes(resource.asByteArray());
    }
}
