package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.ResponseHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StatusCodeResponseHandler implements ResponseHandler {
    private final HttpResponseStatus status;

    public StatusCodeResponseHandler(int code) {
        status = HttpResponseStatus.valueOf(code);
    }

    @Override
    public void writeToResponse(HttpRequest request, HttpResponse response) {
        response.setStatus(status);
    }

    @Override
    public ResponseHandler apply(final MocoConfig config) {
        return this;
    }
}
