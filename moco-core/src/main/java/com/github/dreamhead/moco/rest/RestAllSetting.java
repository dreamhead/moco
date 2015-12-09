package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;

public abstract class RestAllSetting extends RestSetting {
    public RestAllSetting(final Optional<RequestMatcher> matcher,
                          final ResponseHandler handler) {
        super(matcher, handler);
    }

    @Override
    protected RequestMatcher getBaseRequestMatcher(final String resourceName) {
        return by(uri(resourceRoot(resourceName)));
    }
}
