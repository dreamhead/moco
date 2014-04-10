package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.internal.SessionContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StatusCodeResponseHandler extends AbstractResponseHandler {
    private final HttpResponseStatus status;

    public StatusCodeResponseHandler(final int code) {
        status = HttpResponseStatus.valueOf(code);
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        context.getResponse().setStatus(status);
    }
}
