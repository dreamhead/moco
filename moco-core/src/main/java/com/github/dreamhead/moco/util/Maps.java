package com.github.dreamhead.moco.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class Maps {
    /**
     * Unmodifiable copy that keeps insertion order.
     *
     * <p>Named {@code orderedCopyOf} rather than {@code copyOf} on purpose. {@link Map#copyOf}
     * randomises iteration order once per JVM run, so the two are not interchangeable, and a
     * same-named helper invites exactly the substitution that must not happen: header, query, form
     * and cookie maps are rendered in iteration order, so swapping this for {@link Map#copyOf}
     * makes dumps and assertions pass or fail depending on the run.
     */
    public static <K, V> Map<K, V> orderedCopyOf(final Map<? extends K, ? extends V> map) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    /**
     * Collector to an unmodifiable map that keeps encounter order.
     *
     * <p>Deliberately not named {@code toUnmodifiableMap}, for the same reason as
     * {@link #orderedCopyOf}: {@link Collectors#toUnmodifiableMap} randomises iteration order.
     *
     * <p>Duplicate keys raise {@link IllegalArgumentException} to match Guava. This is reachable
     * from user input - a repeated form field or cookie name - and the exception surfaces to
     * {@code MocoMonitor.onException}, so the type must not drift to
     * {@code IllegalStateException} as {@link Collectors#toMap} would give.
     */
    public static <T, K, V> Collector<T, ?, Map<K, V>> toOrderedMap(
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
                .collect(toOrderedMap(Map.Entry::getKey, e -> e.getValue()[0]));
    }

    public static Map<String, String[]> simpleValueToArray(final Map<String, String> map) {
        return map.entrySet()
                .stream()
                .collect(toOrderedMap(Map.Entry::getKey, e -> new String[] {e.getValue()}));
    }

    public static Map<String, String[]> iterableValueToArray(final Map<String, Iterable<String>> map) {
        return map.entrySet()
                .stream()
                .collect(toOrderedMap(Map.Entry::getKey, e -> toArray(e.getValue())));
    }

    private static String[] toArray(final Iterable<String> values) {
        return StreamSupport.stream(values.spliterator(), false).toArray(String[]::new);
    }

    private Maps() {
    }
}
