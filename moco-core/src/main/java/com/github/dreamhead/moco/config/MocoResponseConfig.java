package com.github.dreamhead.moco.config;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;

import static com.github.dreamhead.moco.handler.AndResponseHandler.and;

public final class MocoResponseConfig implements MocoConfig<ResponseHandler> {
    private final ResponseHandler handler;

    public MocoResponseConfig(final ResponseHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean isFor(final String id) {
        return RESPONSE_ID.equalsIgnoreCase(id);
    }

    @Override
    public ResponseHandler apply(final ResponseHandler target) {
        return and(handler, target);
    }
}
