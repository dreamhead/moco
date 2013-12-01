package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.internal.SessionContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StatusCodeResponseHandler implements ResponseHandler {
    private final HttpResponseStatus status;

    public StatusCodeResponseHandler(int code) {
        status = HttpResponseStatus.valueOf(code);
    }

    @Override
    public void writeToResponse(SessionContext context) {
        context.getResponse().setStatus(status);
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return this;
    }
}
