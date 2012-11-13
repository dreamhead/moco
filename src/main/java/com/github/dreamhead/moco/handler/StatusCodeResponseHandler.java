package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class StatusCodeResponseHandler implements ResponseHandler {
    private final HttpResponseStatus status;

    public StatusCodeResponseHandler(int code) {
        status = HttpResponseStatus.valueOf(code);
    }

    @Override
    public void writeToResponse(Channel channel) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        channel.write(response);
    }
}
