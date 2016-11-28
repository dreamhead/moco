package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.base.Function;

import static com.github.dreamhead.moco.util.Iterables.asIterable;
import static com.google.common.collect.FluentIterable.from;

public final class AndResponseHandler extends AbstractResponseHandler {
    private final Iterable<ResponseHandler> handlers;

    private AndResponseHandler(final Iterable<ResponseHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        for (ResponseHandler handler : handlers) {
            handler.writeToResponse(context);
        }
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.RESPONSE_ID)) {
            return super.apply(config);
        }

        return and(from(handlers).transform(applyConfig(config)));
    }

    private Function<ResponseHandler, ResponseHandler> applyConfig(final MocoConfig config) {
        return new Function<ResponseHandler, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final ResponseHandler handler) {
                return handler.apply(config);
            }
        };
    }

    public static ResponseHandler and(final Iterable<ResponseHandler> handlers) {
        return new AndResponseHandler(handlers);
    }

    public static ResponseHandler and(final ResponseHandler handler, final ResponseHandler... handlers) {
        if (handlers.length == 0) {
            return handler;
        }

        return new AndResponseHandler(asIterable(handler, handlers));
    }
}
