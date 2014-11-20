package com.github.dreamhead.moco.config;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.handler.AndResponseHandler;

import static com.google.common.collect.ImmutableList.of;

public class MocoResponseConfig implements MocoConfig<ResponseHandler> {
    private final ResponseHandler handler;

    public MocoResponseConfig(ResponseHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean isFor(final String id) {
        return RESPONSE_ID.equals(id);
    }

    @Override
    public ResponseHandler apply(final ResponseHandler target) {
        return new AndResponseHandler(of(handler, target));
    }
}
