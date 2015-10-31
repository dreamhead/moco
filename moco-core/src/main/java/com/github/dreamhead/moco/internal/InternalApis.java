package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.util.URLs;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.match;
import static com.github.dreamhead.moco.Moco.or;
import static com.github.dreamhead.moco.Moco.uri;

public final class InternalApis {
    public static RequestMatcher context(final String context) {
        return or(by(uri(context)), match(uri(URLs.join(context, ".*"))));
    }

    private InternalApis() {
    }
}
