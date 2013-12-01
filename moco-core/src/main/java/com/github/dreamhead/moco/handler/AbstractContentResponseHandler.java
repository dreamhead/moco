package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.internal.SessionContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.addHeader;

public abstract class AbstractContentResponseHandler extends AbstractResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    protected abstract void writeContentResponse(FullHttpRequest request, ByteBuf buffer);

    @Override
    public void writeToResponse(SessionContext context) {
        this.writeToResponse(context.getRequest(), context.getResponse());
    }

    protected void writeToResponse(FullHttpRequest request, FullHttpResponse response) {
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
