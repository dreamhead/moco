package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;

import static com.google.common.base.Optional.of;

public class HeaderResponseHandler extends AbstractHttpResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    private final String name;
    private final Resource resource;

    public HeaderResponseHandler(final String name, final Resource resource) {
        this.name = name;
        this.resource = resource;
    }


    @Override
    protected void doWriteToResponse(HttpRequest httpRequest, MutableHttpResponse httpResponse) {
        if (detector.hasHeader(httpResponse, name)) {
            httpResponse.removeHeader(name);
        }

        String value = new String(resource.readFor(of(httpRequest)));
        httpResponse.addHeader(name, value);
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        ResponseHandler handler = super.apply(config);
        if (handler != this) {
            return handler;
        }

        Resource resource = this.resource.apply(config);
        if (resource != this.resource) {
            return new HeaderResponseHandler(name, resource);
        }

        return this;
    }
}
