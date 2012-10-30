package com.github.moco;

import com.github.moco.handler.AbstractResponseHandler;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.ByteStreams.toByteArray;

public class StreamResponseHandler extends AbstractResponseHandler {
    private InputStream is;

    public StreamResponseHandler(InputStream is) {
        this.is = is;
    }

    @Override
    protected void writeContent(ChannelBuffer buffer) {
        try {
            buffer.writeBytes(toByteArray(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
