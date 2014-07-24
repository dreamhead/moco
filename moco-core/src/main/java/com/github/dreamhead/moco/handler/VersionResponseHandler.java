package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.*;
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
        Request request = context.getRequest();
        Response response = context.getResponse();
        if (HttpRequest.class.isInstance(request) && MutableHttpResponse.class.isInstance(response)) {
            HttpRequest httpRequest = HttpRequest.class.cast(request);
            MutableHttpResponse httpResponse = MutableHttpResponse.class.cast(response);
            doWriteToResponse(httpRequest, httpResponse);
        }
    }

    private void doWriteToResponse(HttpRequest httpRequest, MutableHttpResponse httpResponse) {
        String version = new String(resource.readFor(of(httpRequest)));
        httpResponse.setVersion(HttpProtocolVersion.versionOf(version));
    }
}
