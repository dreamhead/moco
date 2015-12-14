package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;

public abstract class RestSingleSetting extends SimpleRestSetting {
    private final String id;

    public RestSingleSetting(final String id, final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        super(matcher, handler);
        this.id = id;
    }

    @Override
    protected RequestMatcher getBaseRequestMatcher(final String resourceName) {
        return by(uri(join(resourceRoot(resourceName), this.id)));
    }
}
