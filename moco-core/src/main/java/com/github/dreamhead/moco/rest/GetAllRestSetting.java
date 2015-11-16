package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestSetting;
import com.google.common.base.Optional;

public class GetAllRestSetting extends RestSetting {
    private final RequestMatcher matcher;

    public GetAllRestSetting(final RequestMatcher matcher,
                             final ResponseHandler responseHandler) {
        super(responseHandler);
        this.matcher = matcher;
    }

    public RequestMatcher getMatcher() {
        return matcher;
    }
}
