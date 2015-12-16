package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Optional;

public class HeadAllRestSetting extends RestAllSetting {
    public HeadAllRestSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        super(matcher, handler);
    }
}
