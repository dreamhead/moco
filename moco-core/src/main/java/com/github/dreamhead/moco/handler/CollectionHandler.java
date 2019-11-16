package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.collect.ImmutableList;

import java.util.stream.Collectors;

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
    public final ResponseHandler doApply(final MocoConfig config) {
        return newCollectionHandler(handlers.stream()
                .map(input -> input.apply(config))
                .collect(Collectors.toList()));
    }

    protected abstract int next(int index, int size);

    protected abstract ResponseHandler newCollectionHandler(Iterable<ResponseHandler> handlers);
}
