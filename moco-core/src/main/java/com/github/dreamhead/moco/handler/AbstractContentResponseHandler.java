package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.net.HttpHeaders;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;

import static io.netty.handler.codec.http.HttpHeaders.addHeader;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;

public abstract class AbstractContentResponseHandler extends AbstractResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    protected abstract void writeContentResponse(final HttpRequest request, ByteBuf buffer);

    @Override
    public void writeToResponse(final SessionContext context) {
        FullHttpResponse response = context.getResponse();
        ByteBuf buffer = Unpooled.buffer();
        writeContentResponse(context.getRequest(), buffer);
        response.content().writeBytes(buffer);
        setContentLength(response, response.content().writerIndex());
        if (!detector.hasContentType(response)) {
            addHeader(response, HttpHeaders.CONTENT_TYPE, getContentType(context.getRequest()));
        }
    }

    protected String getContentType(final HttpRequest request) {
        return "text/html; charset=UTF-8";
    }
}
