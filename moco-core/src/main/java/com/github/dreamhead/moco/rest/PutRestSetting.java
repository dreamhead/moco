package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.google.common.base.Optional;

public class PutRestSetting extends RestSingleSetting {
    public PutRestSetting(final String id, final ResponseHandler handler) {
        super(id, handler);
    }

    @Override
    protected Optional<RequestMatcher> doGetRequestMatcher() {
        return Optional.absent();
    }
}
