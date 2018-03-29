package com.github.dreamhead.moco.util;

import com.google.common.base.Predicate;

import java.util.Map;

public final class HttpHeaders {
    private static boolean isSameHeaderName(final String name, final String key) {
        return key.equalsIgnoreCase(name);
    }

    public static Predicate<Map.Entry<String, String[]>> isForHeaderName(final String key) {
        return new Predicate<Map.Entry<String, String[]>>() {
            @Override
            public boolean apply(final Map.Entry<String, String[]> input) {
                return isSameHeaderName(input.getKey(), key);
            }
        };
    }

    private HttpHeaders() {
    }
}
