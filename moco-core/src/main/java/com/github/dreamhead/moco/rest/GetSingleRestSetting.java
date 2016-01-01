package com.github.dreamhead.moco.rest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.RestIdMatcher;
import com.google.common.base.Optional;

public class GetSingleRestSetting extends RestSingleSetting {
    public GetSingleRestSetting(final RestIdMatcher id,
                                final Optional<RequestMatcher> matcher,
                                final ResponseHandler handler) {
        super(id, matcher, handler);
    }
}
