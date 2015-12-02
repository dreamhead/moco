package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Optional;

public class HeadRestSetting extends RestSingleSetting {
    public HeadRestSetting(final String id, final ResponseHandler handler) {
        super(id, Optional.<RequestMatcher>absent(), handler);
    }
}
