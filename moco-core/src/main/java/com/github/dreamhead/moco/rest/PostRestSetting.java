package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Optional;

public class PostRestSetting extends RestAllSetting {
    public PostRestSetting(final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        super(matcher, handler);
    }
}
