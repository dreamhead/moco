package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.handler.codec.http.HttpVersion;

public class VersionResponseHandler implements ResponseHandler {
    private final Resource resource;

    public VersionResponseHandler(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void writeToResponse(SessionContext context) {
        HttpVersion httpVersion = HttpVersion.valueOf(new String(resource.readFor(context.getRequest())));
        context.getResponse().setProtocolVersion(httpVersion);
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return this;
    }
}
