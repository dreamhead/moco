package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.util.URLs;

import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;

public final class RestIds {
    public static String checkId(final String id) {
        return checkValidUrlItem(id, "Rest ID");
    }

    public static String checkResourceName(final String name) {
        return checkValidUrlItem(name, "Resource name");
    }

    private static String checkValidUrlItem(final String id, final String item) {
        checkNotNullOrEmpty(id, item + " should not be null or empty");

        if (id.contains(URLs.SEPARATOR)) {
            throw new IllegalArgumentException(item + " should not contain '/'");
        }

        if (!URLs.isValidUrl(id)) {
            throw new IllegalArgumentException(item + " should not contains invalid URI character");
        }

        return id;
    }

    private RestIds() {
    }
}
