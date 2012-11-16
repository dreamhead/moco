package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.model.ContentStream;
import org.jboss.netty.buffer.ChannelBuffer;

public class ContentHandler extends AbstractResponseHandler {
    private final ContentStream stream;

    public ContentHandler(ContentStream stream) {
        this.stream = stream;
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        buffer.writeBytes(stream.asByteArray());
    }
}
