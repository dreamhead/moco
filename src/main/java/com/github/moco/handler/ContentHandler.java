package com.github.moco.handler;

import org.jboss.netty.buffer.ChannelBuffer;

public class ContentHandler extends AbstractResponseHandler {
    private final String content;

    public ContentHandler(String content) {
        this.content = content;
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        buffer.writeBytes(content.getBytes());
    }
}
