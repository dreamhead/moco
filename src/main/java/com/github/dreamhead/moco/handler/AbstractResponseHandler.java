package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpResponse;

public abstract class AbstractResponseHandler implements ResponseHandler {
    protected abstract void writeContent(ChannelBuffer buffer);

    @Override
    public void writeToResponse(HttpResponse response) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        writeContent(buffer);
        response.setContent(buffer);
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setHeader("Content-Length", response.getContent().writerIndex());
    }
}
