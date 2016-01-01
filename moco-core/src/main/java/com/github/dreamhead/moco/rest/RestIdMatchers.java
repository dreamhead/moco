package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.RestIdMatcher;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.match;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;

public final class RestIdMatchers {
    public static RestIdMatcher anyId() {
        return new RestIdMatcher() {
            @Override
            public RequestMatcher matcher(final String resourceName) {
                return match(uri(join(resourceRoot(resourceName), "[^/]*")));
            }
        };
    }

    public static RestIdMatcher eq(final String id) {
        return new RestIdMatcher() {
            @Override
            public RequestMatcher matcher(final String resourceName) {
                return by(uri(join(resourceRoot(resourceName), id)));
            }
        };
    }

    private RestIdMatchers() {
    }
}
