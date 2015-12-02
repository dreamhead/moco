package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.join;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;

public abstract class RestSingleSetting extends RestSetting {
    private final String id;
    private final Optional<RequestMatcher> matcher;

    public RestSingleSetting(final String id, final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        super(handler);
        this.id = id;
        this.matcher = matcher;
    }

    public RequestMatcher getRequestMatcher(final String resourceName) {
        RequestMatcher idMatcher = by(uri(join(resourceRoot(resourceName), this.id)));

        if (matcher.isPresent()) {
            return and(idMatcher, matcher.get());
        }

        return idMatcher;
    }
}
