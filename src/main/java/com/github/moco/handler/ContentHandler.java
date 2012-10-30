package com.github.moco.handler;

import com.google.common.io.CharStreams;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ContentHandler extends AbstractResponseHandler {
    private final String content;

    public ContentHandler(String content) {
        this.content = content;
    }

    public ContentHandler(InputStream is) {
        try {
            this.content = CharStreams.toString(new InputStreamReader(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        buffer.writeBytes(content.getBytes());
    }
}
