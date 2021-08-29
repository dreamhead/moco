package com.github.dreamhead.moco.util;

import com.google.common.collect.Iterables;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

public final class Maps {
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
