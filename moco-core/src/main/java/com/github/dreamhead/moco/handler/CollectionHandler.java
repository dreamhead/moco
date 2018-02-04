package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.collect.ImmutableList;

import static com.google.common.collect.ImmutableList.copyOf;

public abstract class CollectionHandler extends AbstractResponseHandler {
    protected final ImmutableList<ResponseHandler> handlers;
    private int index;

    protected CollectionHandler(final Iterable<ResponseHandler> handlers) {
        this.handlers = copyOf(handlers);
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        int current = index;
        this.index = next(index, this.handlers.size());
        handlers.get(current).writeToResponse(context);
    }

    protected abstract int next(final int index, final int size);
}
