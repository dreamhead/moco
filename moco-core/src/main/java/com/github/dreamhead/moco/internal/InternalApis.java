package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.RequestMatcher;

import static com.github.dreamhead.moco.Moco.match;
import static com.github.dreamhead.moco.Moco.uri;

public class InternalApis {
    public static RequestMatcher context(final String context) {
        return match(uri(context + ".*"));
    }

    private InternalApis() {}
}
