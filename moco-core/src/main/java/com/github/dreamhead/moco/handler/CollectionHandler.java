package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class CollectionHandler extends AbstractResponseHandler {
    private final List<ResponseHandler> handlers;
    private int index;

    protected CollectionHandler(final Iterable<ResponseHandler> handlers) {
        // List.copyOf takes a Collection, where ImmutableList.copyOf accepted any Iterable.
        this.handlers = StreamSupport.stream(handlers.spliterator(), false).toList();
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
                .toList());
    }

    protected abstract int next(int index, int size);

    protected abstract ResponseHandler newCollectionHandler(Iterable<ResponseHandler> handlers);
}
