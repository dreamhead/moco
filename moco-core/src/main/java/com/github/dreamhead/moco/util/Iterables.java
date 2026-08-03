package com.github.dreamhead.moco.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.List.of;
import static java.util.Objects.requireNonNull;

public final class Iterables {
    public static <T> List<T> asIterable(final T handler, final T[] handlers) {
        requireNonNull(handler);
        requireNonNull(handlers);

        if (handlers.length == 0) {
            return of(handler);
        }

        List<T> result = new ArrayList<>();
        result.add(handler);
        result.addAll(Arrays.asList(handlers));
        return List.copyOf(result);
    }

    public static <T> List<T> asIterable(final T handler, final T handler2, final T[] handlers) {
        requireNonNull(handler);
        requireNonNull(handler2);
        requireNonNull(handlers);

        if (handlers.length == 0) {
            return of(handler, handler2);
        }

        List<T> result = new ArrayList<>();
        result.add(handler);
        result.add(handler2);
        result.addAll(Arrays.asList(handlers));
        return List.copyOf(result);
    }

    public static <T> T head(final T[] elements) {
        requireNonNull(elements);

        if (elements.length == 0) {
            return null;
        }

        return elements[0];
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] tail(final T[] elements) {
        requireNonNull(elements);

        if (elements.length <= 1) {
            return (T[]) Array.newInstance(elements.getClass().getComponentType(), 0);
        }

        return Arrays.copyOfRange(elements, 1, elements.length);
    }

    public static <T> boolean isNullOrEmpty(final Iterable<T> iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }

    private Iterables() {
    }
}
