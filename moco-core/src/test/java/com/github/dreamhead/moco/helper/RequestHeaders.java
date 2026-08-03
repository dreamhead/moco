package com.github.dreamhead.moco.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Header pairs for the test helpers, replacing Guava's {@code ImmutableMultimap}.
 *
 * <p>A plain {@code Map} would not do: some tests deliberately send the same header name twice,
 * for example {@code of("foo", "bar", "foo", "bar2")}, which {@code Map.of} rejects outright.
 */
public final class RequestHeaders {
    public static List<Map.Entry<String, String>> of(final String... nameValuePairs) {
        if (nameValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Header names and values must be paired");
        }

        List<Map.Entry<String, String>> headers = new ArrayList<>(nameValuePairs.length / 2);
        for (int i = 0; i < nameValuePairs.length; i = i + 2) {
            headers.add(Map.entry(nameValuePairs[i], nameValuePairs[i + 1]));
        }

        return List.copyOf(headers);
    }

    private RequestHeaders() {
    }
}
