package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.google.common.base.Optional;

public final class RestSingleSetting extends SimpleRestSetting {
    private final RestIdMatcher id;

    public RestSingleSetting(final HttpMethod method, final RestIdMatcher id,
                             final Optional<RequestMatcher> matcher,
                             final ResponseHandler handler) {
        super(method, matcher, handler);
        this.id = id;
    }

    @Override
    protected RequestMatcher getBaseRequestMatcher(final RestIdMatcher resourceName) {
        return this.id.matcher(resourceName);
    }
}
