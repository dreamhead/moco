package com.github.dreamhead.moco.util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Maps {
    public static ImmutableMap<String, String> asSimple(final Map<String, String[]> map) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            builder.put(entry.getKey(), entry.getValue()[0]);
        }

        return builder.build();
    }

    public static ImmutableMap<String, String[]> asArray(final Map<String, String> map) {
        ImmutableMap.Builder<String, String[]> builder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.put(entry.getKey(), new String[]{entry.getValue()});
        }

        return builder.build();
    }

    private Maps() {
    }
}
