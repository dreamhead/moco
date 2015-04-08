package com.github.dreamhead.moco.util;

import com.google.common.base.Strings;

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

        if (Strings.isNullOrEmpty(path)) {
            return base;
        }

        if (path.startsWith(SEPARATOR)) {
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
