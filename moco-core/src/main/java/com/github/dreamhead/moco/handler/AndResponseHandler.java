package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class AndResponseHandler implements ResponseHandler {
    private final Iterable<ResponseHandler> handlers;

    public AndResponseHandler(Iterable<ResponseHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        for (ResponseHandler handler : handlers) {
            handler.writeToResponse(request, response);
        }
    }

    @Override
    public void apply(MocoConfig config) {
        for (ResponseHandler handler : handlers) {
            handler.apply(config);
        }
    }
}
