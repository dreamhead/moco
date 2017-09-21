package com.github.dreamhead.moco.config;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;

import static com.github.dreamhead.moco.Moco.and;

public final class MocoRequestConfig implements MocoConfig<RequestMatcher> {
    private final RequestMatcher requestMatcher;

    public MocoRequestConfig(final RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    @Override
    public boolean isFor(final String id) {
        return REQUEST_ID.equalsIgnoreCase(id);
    }

    @Override
    public RequestMatcher apply(final RequestMatcher target) {
        return and(requestMatcher, target);
    }
}
