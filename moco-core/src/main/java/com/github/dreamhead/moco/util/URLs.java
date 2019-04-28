package com.github.dreamhead.moco.util;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Function;
import com.google.common.base.Strings;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;

public final class URLs {
    public static final String SEPARATOR = "/";

    public static String resourceRoot(final String name) {
        if (name.startsWith(SEPARATOR)) {
            return name;
        }

        return SEPARATOR + name;
    }

    public static String toBase(final String baseUri) {
        if (baseUri.endsWith(SEPARATOR)) {
            return baseUri;
        }

        return baseUri + SEPARATOR;
    }

    public static String join(final String base, final String... paths) {
        String target = base;
        for (String path : paths) {
            target = doJoin(target, path);
        }

        return target;
    }

    private static String doJoin(final String base, final String path) {
        String joinPath = toJoinPath(path);
        if (base.endsWith(SEPARATOR)) {
            return base + joinPath;
        }

        if (joinPath.isEmpty()) {
            return base;
        }

        return base + SEPARATOR + joinPath;
    }

    private static String toJoinPath(final String path) {
        if (Strings.isNullOrEmpty(path)) {
            return "";
        }

        if (path.startsWith(SEPARATOR)) {
            return path.substring(1);
        }

        return path;
    }

    public static URL toUrl(final String url) {
        try {
            return new URL(checkNotNullOrEmpty(url, "URL should not be null"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Function<HttpRequest, URL> toUrlFunction(final Resource url) {
        return new Function<HttpRequest, URL>() {
            @Override
            public URL apply(final HttpRequest input) {
                return toUrl(url.readFor(input).toString());
            }
        };
    }

    private URLs() {
    }

    public static boolean isValidUrl(final String url) {
        try {
            String encodedURL = URLEncoder.encode(url, Charset.defaultCharset().name());
            return url.equals(encodedURL);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }
}
