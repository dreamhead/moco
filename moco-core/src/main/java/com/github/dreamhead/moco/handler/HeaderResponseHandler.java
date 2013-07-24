package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class HeaderResponseHandler implements ResponseHandler {
    private final ContentTypeDetector detector = new ContentTypeDetector();

    private final String name;
    private final Resource resource;

    public HeaderResponseHandler(String name, Resource resource) {
        this.name = name;
        this.resource = resource;
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        if (detector.hasHeader(response, name)) {
            response.headers().remove(name);
        }

        HttpHeaders.addHeader(response, name, new String(resource.readFor(request)));
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(resource.id())) {
            return new HeaderResponseHandler(name, resource.apply(config));
        }

        return this;
    }
}
