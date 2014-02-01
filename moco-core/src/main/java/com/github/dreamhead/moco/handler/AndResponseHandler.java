package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.base.Function;

import static com.google.common.collect.FluentIterable.from;

public class AndResponseHandler extends AbstractResponseHandler {
    private final Iterable<ResponseHandler> handlers;

    public AndResponseHandler(Iterable<ResponseHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void writeToResponse(SessionContext context) {
        for (ResponseHandler handler : handlers) {
            handler.writeToResponse(context);
        }
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.RESPONSE_ID)) {
            return super.apply(config);
        }

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
