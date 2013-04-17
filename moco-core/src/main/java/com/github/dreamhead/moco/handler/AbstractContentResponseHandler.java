package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public abstract class AbstractContentResponseHandler implements ResponseHandler {
    private ContentTypeDetector detector = new ContentTypeDetector();

    protected abstract void writeContentResponse(HttpRequest request, ChannelBuffer buffer);

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        writeContentResponse(request, buffer);
        response.setContent(buffer);
        response.setHeader("Content-Length", response.getContent().writerIndex());
        if (!detector.hasContentType(response)) {
            response.setHeader("Content-Type", "text/plain; charset=UTF-8");
        }

    }

}
