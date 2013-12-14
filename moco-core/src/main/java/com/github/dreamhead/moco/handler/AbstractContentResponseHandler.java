package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.internal.SessionContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.addHeader;

public abstract class AbstractContentResponseHandler extends AbstractResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    protected abstract void writeContentResponse(HttpRequest request, ByteBuf buffer);

    @Override
    public void writeToResponse(SessionContext context) {
        FullHttpResponse response = context.getResponse();
        ByteBuf buffer = Unpooled.buffer();
        writeContentResponse(context.getRequest(), buffer);
        response.content().writeBytes(buffer);
        addHeader(response, CONTENT_LENGTH, buffer.writerIndex());
        if (!detector.hasContentType(response)) {
            addHeader(response, CONTENT_TYPE, getContentType(context.getRequest()));
        }
    }

    protected String getContentType(HttpRequest request) {
        return "text/html; charset=UTF-8";
    }
}
