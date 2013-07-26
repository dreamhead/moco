package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class AbstractContentResponseHandler implements ResponseHandler {
    private final ContentTypeDetector detector = new ContentTypeDetector();

    protected abstract void writeContentResponse(FullHttpRequest request, ByteBuf buffer);

    @Override
    public void writeToResponse(FullHttpRequest request, FullHttpResponse response) {
        ByteBuf buffer = Unpooled.buffer();
        writeContentResponse(request, buffer);
        response.content().writeBytes(buffer);
        response.headers().set("Content-Length", buffer.writerIndex());
        if (!detector.hasContentType(response)) {
            response.headers().set("Content-Type", getContentType(request));
        }
    }

    protected String getContentType(FullHttpRequest request) {
        return "text/html; charset=UTF-8";
    }
}
