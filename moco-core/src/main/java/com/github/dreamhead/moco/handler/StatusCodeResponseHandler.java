package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StatusCodeResponseHandler extends AbstractHttpResponseHandler {
    private final HttpResponseStatus status;

    public StatusCodeResponseHandler(final int code) {
        status = HttpResponseStatus.valueOf(code);
    }

    @Override
    protected void doWriteToResponse(HttpRequest httpRequest, MutableHttpResponse httpResponse) {
        httpResponse.setStatus(status.code());
    }
}
