package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

public class GetAllRestSetting extends RestSetting {
    private final Optional<RequestMatcher> matcher;

    public GetAllRestSetting(final Optional<RequestMatcher> matcher,
                             final ResponseHandler responseHandler) {
        super(responseHandler);
        this.matcher = matcher;
    }

    public Optional<RequestMatcher> getMatcher() {
        return matcher;
    }
}
