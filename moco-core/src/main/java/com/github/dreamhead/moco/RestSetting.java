package com.github.dreamhead.moco;

import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.and;

public abstract class RestSetting {
    private final Optional<RequestMatcher> matcher;
    private final ResponseHandler handler;

    protected abstract RequestMatcher getBaseRequestMatcher(final String resourceName);

    public RestSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        this.matcher = matcher;
        this.handler = handler;
    }

    public ResponseHandler getHandler() {
        return handler;
    }

    public RequestMatcher getRequestMatcher(final String resourceName) {
        RequestMatcher rootMatcher = getBaseRequestMatcher(resourceName);
        if (this.matcher.isPresent()) {
            return and(rootMatcher, this.matcher.get());
        }

        return rootMatcher;
    }
}
