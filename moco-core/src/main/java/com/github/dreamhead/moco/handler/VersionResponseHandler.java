package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class VersionResponseHandler implements ResponseHandler {
    private final HttpVersion httpVersion;

    public VersionResponseHandler(String version) {
        this.httpVersion = HttpVersion.valueOf(version);
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        response.setProtocolVersion(httpVersion);
    }
}
