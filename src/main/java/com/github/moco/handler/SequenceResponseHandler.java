package com.github.moco.handler;

import org.jboss.netty.buffer.ChannelBuffer;

public class SequenceResponseHandler extends AbstractResponseHandler {
    private String[] contents;
    private int index;

    public SequenceResponseHandler(String[] contents) {
        this.contents = contents;
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        buffer.writeBytes(contents[current()].getBytes());
    }

    private int current() {
        int current = this.index;
        if (++index >= contents.length) {
            index = contents.length - 1;
        }

        return current;
    }
}
