package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;

public abstract class AbstractResponseHandler implements ResponseHandler {
    @Override
    @SuppressWarnings("unchecked")
    public final ResponseHandler apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.RESPONSE_ID)) {
            return (ResponseHandler) config.apply(this);
        }

        return this;
    }
}
