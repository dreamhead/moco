package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.internal.SessionContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StatusCodeResponseHandler extends AbstractResponseHandler {
    private final HttpResponseStatus status;

    public StatusCodeResponseHandler(final int code) {
        status = HttpResponseStatus.valueOf(code);
    }

    @Override
    public void writeToResponse(final SessionContext context) {
        Request request = context.getRequest();
        Response response = context.getResponse();
        if (HttpRequest.class.isInstance(request) && MutableHttpResponse.class.isInstance(response)) {
            HttpRequest httpRequest = HttpRequest.class.cast(request);
            MutableHttpResponse httpResponse = MutableHttpResponse.class.cast(response);
            doWriteToResponse(httpResponse);
        }
    }

    private void doWriteToResponse(MutableHttpResponse httpResponse) {
        httpResponse.setStatus(status.code());
    }
}
