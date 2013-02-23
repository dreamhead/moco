package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class HeaderResponseHandler implements ResponseHandler {
    private final String name;
    private final String value;

    public HeaderResponseHandler(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        response.setHeader(name, value);
    }
}
