package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.dreamhead.moco.util.Iterables.asIterable;

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
    public ResponseHandler doApply(final MocoConfig config) {
        return and(StreamSupport.stream(handlers.spliterator(), false)
                .map(handler -> handler.apply(config))
                .collect(Collectors.toList()));
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
