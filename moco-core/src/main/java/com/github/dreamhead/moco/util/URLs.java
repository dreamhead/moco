package com.github.dreamhead.moco.util;

import java.net.MalformedURLException;
import java.net.URL;

import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;

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

    public static URL toUrl(final String url) {
        try {
            return new URL(checkNotNullOrEmpty(url, "URL should not be null"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private URLs() {}
}
