package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public abstract class AbstractContentResponseHandler implements ResponseHandler {
    private final ContentTypeDetector detector = new ContentTypeDetector();

    protected abstract void writeContentResponse(HttpRequest request, ChannelBuffer buffer);

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        writeContentResponse(request, buffer);
        response.setContent(buffer);
        response.setHeader("Content-Length", response.getContent().writerIndex());
        if (!detector.hasContentType(response)) {
            response.setHeader("Content-Type", getContentType(request));
        }
    }

    protected String getContentType(HttpRequest request) {
        return "text/html; charset=UTF-8";
    }
}
