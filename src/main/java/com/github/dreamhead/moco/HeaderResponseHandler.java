package com.github.dreamhead.moco;

import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class HeaderResponseHandler implements ResponseHandler {
    private String name;
    private String value;

    public HeaderResponseHandler(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public HttpResponse createResponse() {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setHeader(name, value);
        return response;
    }
}
