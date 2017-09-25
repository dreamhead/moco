package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;

public final class RestAllSetting extends SimpleRestSetting {
    public RestAllSetting(final HttpMethod method,
                          final Optional<RequestMatcher> matcher,
                          final ResponseHandler handler) {
        super(method, matcher, handler);
    }

    @Override
    protected RequestMatcher getBaseRequestMatcher(final RestIdMatcher resourceName) {
        return by(uri(resourceRoot(resourceName.resourceUri())));
    }
}
