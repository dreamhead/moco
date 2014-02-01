package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;

public class SequenceContentHandler extends AbstractResponseHandler {
    private final ResponseHandler[] handlers;
    private int index;

    public SequenceContentHandler(final ResponseHandler[] handlers) {
        this.handlers = handlers;
    }

    @Override
    public void writeToResponse(SessionContext context) {
        handlers[current()].writeToResponse(context);
    }

    private int current() {
        int current = this.index;
        if (++index >= handlers.length) {
            index = handlers.length - 1;
        }

        return current;
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.RESPONSE_ID)) {
            return super.apply(config);
        }

        FluentIterable<ResponseHandler> transformedResources = from(copyOf(handlers)).transform(applyConfig(config));
        return new SequenceContentHandler(transformedResources.toArray(ResponseHandler.class));
    }

    private Function<ResponseHandler, ResponseHandler> applyConfig(final MocoConfig config) {
        return new Function<ResponseHandler, ResponseHandler>() {
            @Override
            public ResponseHandler apply(ResponseHandler input) {
                return input.apply(config);
            }
        };
    }
}
