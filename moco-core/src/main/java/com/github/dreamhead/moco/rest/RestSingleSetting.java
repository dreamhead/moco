package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.google.common.base.Optional;

public abstract class RestSingleSetting extends SimpleRestSetting {
    private final RestIdMatcher id;

    public RestSingleSetting(final RestIdMatcher id, final Optional<RequestMatcher> matcher,
                             final ResponseHandler handler) {
        super(matcher, handler);
        this.id = id;
    }

    @Override
    protected RequestMatcher getBaseRequestMatcher(final String resourceName) {
        return this.id.matcher(resourceName);
    }
}
