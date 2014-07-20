package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;

import static com.google.common.base.Optional.of;

public class HeaderResponseHandler extends AbstractResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    private final String name;
    private final Resource resource;

    public HeaderResponseHandler(final String name, final Resource resource) {
        this.name = name;
        this.resource = resource;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        FullHttpResponse response = context.getResponse();
        if (detector.hasHeader(response, name)) {
            response.headers().remove(name);
        }

        String value = new String(resource.readFor(of(context.getRequest())));
        HttpHeaders.addHeader(response, name, value);

        context.getHttpResponse().addHeader(name, value);
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
