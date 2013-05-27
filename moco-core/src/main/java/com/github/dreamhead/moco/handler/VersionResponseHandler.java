package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.VersionResource;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class VersionResponseHandler implements ResponseHandler {
    private final HttpVersion httpVersion;

    public VersionResponseHandler(VersionResource resource) {
        this.httpVersion = HttpVersion.valueOf(new String(resource.asByteArray()));
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        response.setProtocolVersion(httpVersion);
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return this;
    }
}
