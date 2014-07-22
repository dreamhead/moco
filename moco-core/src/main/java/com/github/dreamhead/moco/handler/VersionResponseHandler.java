package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.resource.Resource;

import static com.google.common.base.Optional.of;

public class VersionResponseHandler extends AbstractResponseHandler {
    private final Resource resource;

    public VersionResponseHandler(final Resource resource) {
        this.resource = resource;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        String version = new String(resource.readFor(of(context.getRequest())));
        context.getHttpResponse().setVersion(HttpProtocolVersion.versionOf(version));
    }
}
