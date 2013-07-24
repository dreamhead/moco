package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Function;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import static com.google.common.collect.FluentIterable.from;

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
    public ResponseHandler apply(final MocoConfig config) {
        return new AndResponseHandler(from(handlers).transform(applyConfig(config)));
    }

    private Function<ResponseHandler, ResponseHandler> applyConfig(final MocoConfig config) {
        return new Function<ResponseHandler, ResponseHandler>() {
            @Override
            public ResponseHandler apply(ResponseHandler handler) {
                return handler.apply(config);
            }
        };
    }
}
