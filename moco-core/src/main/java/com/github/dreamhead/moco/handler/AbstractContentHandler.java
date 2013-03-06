package com.github.dreamhead.moco.handler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpRequest;

public abstract class AbstractContentHandler extends AbstractContentResponseHandler {
    protected abstract void writeContent(ChannelBuffer buffer);

    @Override
    protected void writeContentResponse(HttpRequest request, ChannelBuffer buffer) {
        this.writeContent(buffer);
    }
}
