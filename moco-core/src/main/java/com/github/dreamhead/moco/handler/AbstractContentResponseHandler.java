package com.github.dreamhead.moco.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.addHeader;

public abstract class AbstractContentResponseHandler extends AbstractResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    protected abstract void writeContentResponse(FullHttpRequest request, ByteBuf buffer);

    @Override
    public void writeToResponse(FullHttpRequest request, FullHttpResponse response) {
        ByteBuf buffer = Unpooled.buffer();
        writeContentResponse(request, buffer);
        response.content().writeBytes(buffer);
        addHeader(response, CONTENT_LENGTH, buffer.writerIndex());
        if (!detector.hasContentType(response)) {
            addHeader(response, CONTENT_TYPE, getContentType(request));
        }
    }

    protected String getContentType(FullHttpRequest request) {
        return "text/html; charset=UTF-8";
    }
}
