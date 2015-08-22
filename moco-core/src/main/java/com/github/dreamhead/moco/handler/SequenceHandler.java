package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;

public class SequenceHandler extends AbstractResponseHandler {
    private final ImmutableList<ResponseHandler> handlers;
    private int index;

    public SequenceHandler(final ImmutableList<ResponseHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        handlers.get(current()).writeToResponse(context);
    }

    private int current() {
        int current = this.index;
        if (++index >= handlers.size()) {
            index = handlers.size() - 1;
        }

        return current;
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.RESPONSE_ID)) {
            return super.apply(config);
        }

        FluentIterable<ResponseHandler> transformedResources = from(copyOf(handlers)).transform(applyConfig(config));
        return new SequenceHandler(transformedResources.toList());
    }

    private Function<ResponseHandler, ResponseHandler> applyConfig(final MocoConfig config) {
        return new Function<ResponseHandler, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final ResponseHandler input) {
                return input.apply(config);
            }
        };
    }
}
