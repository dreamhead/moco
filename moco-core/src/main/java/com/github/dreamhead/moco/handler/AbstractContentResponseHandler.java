package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public abstract class AbstractContentResponseHandler implements ResponseHandler {
    protected abstract void writeContentResponse(HttpRequest request, ChannelBuffer buffer);

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        writeContentResponse(request, buffer);
        response.setContent(buffer);
        if (!hasContentType(response)) {
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
        }
        response.setHeader("Content-Length", response.getContent().writerIndex());
    }

    private boolean hasContentType(HttpResponse response) {
        for (String header : response.getHeaderNames()) {
            if (header.equalsIgnoreCase("Content-Type")) {
                return true;
            }
        }
        return false;
    }
}
