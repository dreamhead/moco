package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.checkArgument;

public final class CycleHandler extends CollectionHandler {
    private CycleHandler(final Iterable<ResponseHandler> handlers) {
        super(handlers);
    }

    public static ResponseHandler newCycle(final Iterable<ResponseHandler> handlers) {
        checkArgument(!Iterables.isEmpty(handlers), "Cycle contents should not be null");
        return new CycleHandler(handlers);
    }

    @Override
    protected int next(final int index, final int size) {
        int next = index + 1;
        if (next >= size) {
            next = 0;
        }

        return next;
    }

    protected ResponseHandler newCollectionHandler(final Iterable<ResponseHandler> handlers) {
        return new CycleHandler(handlers);
    }
}
