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
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        if (detector.hasHeader(httpResponse, name)) {
            httpResponse.removeHeader(name);
        }

        String value = resource.readFor(of(httpRequest)).toString();
        httpResponse.addHeader(name, value);
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        Resource appliedResource = this.resource.apply(config);
        if (appliedResource != this.resource) {
            return new HeaderResponseHandler(name, appliedResource);
        }

        return super.apply(config);
    }
}
