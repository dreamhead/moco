package com.github.dreamhead.moco.util;

import com.google.common.collect.ImmutableList;

import static com.google.common.collect.ImmutableList.of;

public final class Iterables {
    public static <T> Iterable<T> asIterable(final T handler, final T[] handlers) {
        if (handlers.length == 0) {
            return of(handler);
        }

        return ImmutableList.<T>builder()
                .add(handler)
                .add(handlers)
                .build();
    }

    private Iterables() {
    }
}
