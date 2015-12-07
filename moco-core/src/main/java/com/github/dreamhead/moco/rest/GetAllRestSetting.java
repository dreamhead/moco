package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.util.URLs.resourceRoot;

public class GetAllRestSetting extends RestSetting {
    private final Optional<RequestMatcher> matcher;

    public GetAllRestSetting(final Optional<RequestMatcher> matcher,
                             final ResponseHandler responseHandler) {
        super(responseHandler);
        this.matcher = matcher;
    }

    public RequestMatcher getRequestMatcher(final String resourceName) {
        RequestMatcher rootMatcher = by(uri(resourceRoot(resourceName)));
        if (this.matcher.isPresent()) {
            return and(rootMatcher, this.matcher.get());
        }

        return rootMatcher;
    }
}
