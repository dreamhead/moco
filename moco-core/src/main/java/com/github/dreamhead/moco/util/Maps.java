package com.github.dreamhead.moco.util;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public class Maps {
    public static ImmutableMap<String, String> arrayValueToSimple(final Map<String, String[]> map) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            builder.put(entry.getKey(), entry.getValue()[0]);
        }

        return builder.build();
    }

    public static ImmutableMap<String, String[]> simpleValueToArray(final Map<String, String> map) {
        ImmutableMap.Builder<String, String[]> builder = ImmutableMap.builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.put(entry.getKey(), new String[]{entry.getValue()});
        }

        return builder.build();
    }

    public static ImmutableMap<String, String[]> listValueToArray(final Map<String, List<String>> map) {
        ImmutableMap.Builder<String, String[]> builder = ImmutableMap.builder();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> value = entry.getValue();
            builder.put(entry.getKey(), value.toArray(new String[value.size()]));
        }

        return builder.build();
    }

    private Maps() {
    }
}
