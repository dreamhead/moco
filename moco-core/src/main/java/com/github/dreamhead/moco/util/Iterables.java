package com.github.dreamhead.moco.util;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;

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

    public static <T> T head(final T[] elements) {
        return elements[0];
    }

    public static <T> T[] tail(final T[] elements) {
        return Arrays.copyOfRange(elements, 1, elements.length);
    }

    private Iterables() {
    }
}
