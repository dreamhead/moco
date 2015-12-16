package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Optional;

public class HeadSingleRestSetting extends RestSingleSetting {
    public HeadSingleRestSetting(final String id, final Optional<RequestMatcher> matcher, final ResponseHandler handler) {
        super(id, matcher, handler);
    }
}
