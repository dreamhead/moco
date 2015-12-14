package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.and;

public abstract class SimpleRestSetting implements RestSetting {
    private final Optional<RequestMatcher> matcher;
    private final ResponseHandler handler;

    protected abstract RequestMatcher getBaseRequestMatcher(final String resourceName);

    public SimpleRestSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
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
