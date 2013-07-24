package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public abstract class AbstractContentResponseHandler implements ResponseHandler {
    private final ContentTypeDetector detector = new ContentTypeDetector();

    protected abstract void writeContentResponse(HttpRequest request, ByteBuf buffer);

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        ByteBuf buffer = Unpooled.buffer();
        if (response instanceof HttpContent) {
            HttpContent content = (HttpContent) response;
            writeContentResponse(request, buffer);
            content.content().writeBytes(buffer);
            response.headers().set("Content-Length", buffer.writerIndex());
            if (!detector.hasContentType(response)) {
                response.headers().set("Content-Type", getContentType(request));
            }
            return;
        }

        throw new IllegalArgumentException("Response with content is expected");


//                ByteBuf.buffer();
//        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
//        writeContentResponse(request, buffer);
//        response.setContent(buffer);
//        response.setHeader("Content-Length", response.getContent().writerIndex());
//        if (!detector.hasContentType(response)) {
//            response.setHeader("Content-Type", getContentType(request));
//        }
    }

    protected String getContentType(HttpRequest request) {
        return "text/html; charset=UTF-8";
    }
}
