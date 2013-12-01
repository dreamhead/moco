package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;

public class VersionResponseHandler implements ResponseHandler {
    private final Resource resource;

    public VersionResponseHandler(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void writeToResponse(SessionContext context) {
        this.writeToResponse(context.getRequest(), context.getResponse());
    }

    private void writeToResponse(FullHttpRequest request, FullHttpResponse response) {
        HttpVersion httpVersion = HttpVersion.valueOf(new String(resource.readFor(request)));
        response.setProtocolVersion(httpVersion);
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return this;
    }
}
