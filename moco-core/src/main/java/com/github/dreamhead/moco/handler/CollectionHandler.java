package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.ImmutableList.copyOf;

public abstract class CollectionHandler extends AbstractResponseHandler {
    private final ImmutableList<ResponseHandler> handlers;
    private int index;

    protected CollectionHandler(final Iterable<ResponseHandler> handlers) {
        this.handlers = copyOf(handlers);
    }

    @Override
    public final void writeToResponse(final SessionContext context) {
        int current = index;
        this.index = next(index, this.handlers.size());
        handlers.get(current).writeToResponse(context);
    }

    @Override
    public final ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.RESPONSE_ID)) {
            return super.apply(config);
        }

        FluentIterable<ResponseHandler> transformedResources = from(copyOf(handlers)).transform(applyConfig(config));
        return newCollectionHandler(transformedResources);
    }

    private Function<ResponseHandler, ResponseHandler> applyConfig(final MocoConfig config) {
        return new Function<ResponseHandler, ResponseHandler>() {
            @Override
            public ResponseHandler apply(final ResponseHandler input) {
                return input.apply(config);
            }
        };
    }

    protected abstract int next(int index, int size);

    protected abstract ResponseHandler newCollectionHandler(Iterable<ResponseHandler> handlers);
}
