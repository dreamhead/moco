package com.github.dreamhead.moco.util;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.of;

public final class Iterables {
    public static <T> Iterable<T> asIterable(final T handler, final T[] handlers) {
        checkNotNull(handler);
        checkNotNull(handlers);

        if (handlers.length == 0) {
            return of(handler);
        }

        return ImmutableList.<T>builder()
                .add(handler)
                .add(handlers)
                .build();
    }

    public static <T> Iterable<T> asIterable(final T handler, final T handler2, final T[] handlers) {
        checkNotNull(handler);
        checkNotNull(handler2);
        checkNotNull(handlers);

        if (handlers.length == 0) {
            return of(handler, handler2);
        }

        return ImmutableList.<T>builder()
                .add(handler)
                .add(handler2)
                .add(handlers)
                .build();
    }

    public static <T> T head(final T[] elements) {
        checkNotNull(elements);

        if (elements.length == 0) {
            return null;
        }

        return elements[0];
    }

    public static <T> T[] tail(final T[] elements) {
        checkNotNull(elements);
        return Arrays.copyOfRange(elements, 1, elements.length);
    }

    private Iterables() {
    }
}
