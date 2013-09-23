package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;

public abstract class AbstractResponseHandler implements ResponseHandler {
    @Override
    public ResponseHandler apply(MocoConfig config) {
        return this;
    }
}
