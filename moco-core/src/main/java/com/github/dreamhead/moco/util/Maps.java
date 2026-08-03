package com.github.dreamhead.moco.util;

import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

public final class Maps {
    /**
     * Order-preserving replacement for Guava's {@code ImmutableMap.copyOf}.
     *
     * <p>{@link Map#copyOf} is deliberately not used: it randomises iteration order once per JVM
     * run, while header, query, form and cookie maps are dumped and rendered in iteration order.
     */
    public static <K, V> Map<K, V> copyOf(final Map<? extends K, ? extends V> map) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    /**
     * Order-preserving collector replacing Guava's {@code ImmutableMap.toImmutableMap}.
     *
     * <p>Duplicate keys raise {@link IllegalArgumentException} to match Guava. This is reachable
     * from user input - a repeated form field or cookie name - and the exception surfaces to
     * {@code MocoMonitor.onException}, so the type must not drift to
     * {@code IllegalStateException} as {@link Collectors#toMap} would give.
     */
    public static <T, K, V> Collector<T, ?, Map<K, V>> toUnmodifiableMap(
            final Function<? super T, ? extends K> keyMapper,
            final Function<? super T, ? extends V> valueMapper) {
        Collector<T, ?, LinkedHashMap<K, V>> collector = Collectors.<T, K, V, LinkedHashMap<K, V>>toMap(
                keyMapper, valueMapper, Maps::rejectDuplicate, LinkedHashMap::new);
        return Collectors.collectingAndThen(collector, Collections::unmodifiableMap);
    }

    private static <V> V rejectDuplicate(final V first, final V second) {
        throw new IllegalArgumentException("Multiple entries with same key: " + first + " and " + second);
    }

    public static Map<String, String> arrayValueToSimple(final Map<String, String[]> map) {
        return map.entrySet()
                .stream()
                .collect(toImmutableMap(Map.Entry::getKey, e -> e.getValue()[0]));
    }

    public static Map<String, String[]> simpleValueToArray(final Map<String, String> map) {
        return map.entrySet()
                .stream()
                .collect(toImmutableMap(Map.Entry::getKey, e -> new String[] {e.getValue()}));
    }

    public static Map<String, String[]> iterableValueToArray(final Map<String, Iterable<String>> map) {
        return map.entrySet()
                .stream()
                .collect(toImmutableMap(Map.Entry::getKey, e -> Iterables.toArray(e.getValue(), String.class)));
    }

    private Maps() {
    }
}
