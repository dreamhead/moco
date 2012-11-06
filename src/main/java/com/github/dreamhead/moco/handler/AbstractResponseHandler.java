package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public abstract class AbstractResponseHandler implements ResponseHandler {
    @Override
    public void writeToResponse(Channel channel) {
        channel.write(createResponse());
    }

    private HttpResponse createResponse() {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        writeContent(buffer);
        response.setContent(buffer);
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setHeader("Content-Length", response.getContent().writerIndex());
        return response;
    }

    protected abstract void writeContent(ChannelBuffer buffer);
}
