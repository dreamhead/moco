package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Optional;

public class GetAllRestSetting extends RestAllSetting {
    public GetAllRestSetting(final Optional<RequestMatcher> matcher,
                             final ResponseHandler responseHandler) {
        super(matcher, responseHandler);
    }
}
