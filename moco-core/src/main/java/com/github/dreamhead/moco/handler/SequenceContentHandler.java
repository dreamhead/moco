package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.resource.Resource;
import org.jboss.netty.buffer.ChannelBuffer;

public class SequenceContentHandler extends AbstractContentHandler {
    private final Resource[] resources;
    private int index;

    public SequenceContentHandler(final Resource[] resources) {
        this.resources = resources;
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        buffer.writeBytes(resources[current()].asByteArray());
    }

    private int current() {
        int current = this.index;
        if (++index >= resources.length) {
            index = resources.length - 1;
        }

        return current;
    }
}
