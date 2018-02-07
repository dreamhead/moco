package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.checkArgument;

public final class SequenceHandler extends CollectionHandler {
    private SequenceHandler(final Iterable<ResponseHandler> handlers) {
        super(handlers);
    }

    public static ResponseHandler newSeq(final Iterable<ResponseHandler> handlers) {
        checkArgument(!Iterables.isEmpty(handlers), "Sequence contents should not be null");
        return new SequenceHandler(handlers);
    }

    @Override
    protected int next(final int index, final int size) {
        int next = index + 1;
        if (next >= size) {
            next = size - 1;
        }

        return next;
    }

    @Override
    protected ResponseHandler newCollectionHandler(final Iterable<ResponseHandler> handlers) {
        return new SequenceHandler(handlers);
    }
}
