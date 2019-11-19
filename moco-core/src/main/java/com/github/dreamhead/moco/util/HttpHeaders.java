package com.github.dreamhead.moco.util;

public final class HttpHeaders {
    public static boolean isSameHeaderName(final String name, final String key) {
        return key.equalsIgnoreCase(name);
    }

    private HttpHeaders() {
    }
}
