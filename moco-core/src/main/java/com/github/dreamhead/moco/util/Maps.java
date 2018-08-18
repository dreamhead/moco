package com.github.dreamhead.moco.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import java.util.Map;

public final class Maps {
    public static Map<String, String> arrayValueToSimple(final Map<String, String[]> map) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            builder.put(entry.getKey(), entry.getValue()[0]);
        }

        return builder.build();
    }

    public static Map<String, String[]> simpleValueToArray(final Map<String, String> map) {
        ImmutableMap.Builder<String, String[]> builder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.put(entry.getKey(), new String[]{entry.getValue()});
        }

        return builder.build();
    }

    public static Map<String, String[]> iterableValueToArray(final Map<String, Iterable<String>> map) {
        ImmutableMap.Builder<String, String[]> builder = ImmutableMap.builder();
        for (Map.Entry<String, Iterable<String>> entry : map.entrySet()) {
            Iterable<String> value = entry.getValue();
            builder.put(entry.getKey(), Iterables.toArray(value, String.class));
        }

        return builder.build();
    }

    private Maps() {
    }
}
