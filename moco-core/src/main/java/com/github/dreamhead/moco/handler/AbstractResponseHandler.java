package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.ResponseHandler;

public abstract class AbstractResponseHandler implements ResponseHandler {
    public ResponseHandler doApply(final MocoConfig config) {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.RESPONSE_ID)) {
            return (ResponseHandler) config.apply(this);
        }

        return doApply(config);
    }
}
