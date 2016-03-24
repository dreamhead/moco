package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.util.URLs;

import static com.github.dreamhead.moco.util.Preconditions.checkNotNullOrEmpty;

public class RestIds {
    public static String checkId(final String id) {
        checkNotNullOrEmpty(id, "ID should not be null or empty");

        if (id.contains("/")) {
            throw new IllegalArgumentException("REST ID should not contain '/'");
        }

        if (!URLs.isValidUrl(id)) {
            throw new IllegalArgumentException("ID should not contains invalid URI character");
        }

        return id;
    }

    private RestIds() {
    }
}
