package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.model.ContentStream;
import org.jboss.netty.buffer.ChannelBuffer;

public class SequenceResponseHandler extends AbstractResponseHandler {
    private ContentStream[] contents;
    private int index;

    public SequenceResponseHandler(ContentStream[] contents) {
        this.contents = contents;
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        buffer.writeBytes(contents[current()].asByteArray());
    }

    private int current() {
        int current = this.index;
        if (++index >= contents.length) {
            index = contents.length - 1;
        }

        return current;
    }
}
