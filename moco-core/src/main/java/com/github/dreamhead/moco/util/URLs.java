package com.github.dreamhead.moco.util;

public class URLs {
    private static final String SEPARATOR = "/";

    public static String toBase(String baseUri) {
        if (baseUri.endsWith(SEPARATOR)) {
            return baseUri;
        }

        return baseUri + SEPARATOR;
    }

    public static String join(String base, String path) {
        if (base.endsWith(SEPARATOR)) {
            return base + path;
        }

        return base + SEPARATOR + path;
    }

    private URLs() {}
}
