package com.github.dreamhead.moco.util;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.isEmpty;

public final class Iterables {
    public static <T> List<T> asIterable(final T handler, final T[] handlers) {
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

    public static <T> List<T> asIterable(final T handler, final T handler2, final T[] handlers) {
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

    @SuppressWarnings("unchecked")
    public static <T> T[] tail(final T[] elements) {
        checkNotNull(elements);

        if (elements.length <= 1) {
            return (T[]) Array.newInstance(elements.getClass().getComponentType(), 0);
        }

        return Arrays.copyOfRange(elements, 1, elements.length);
    }

    public static <T> boolean isNullOrEmpty(final Iterable<T> iterable) {
        return iterable == null || isEmpty(iterable);
    }

    private Iterables() {
    }
}
