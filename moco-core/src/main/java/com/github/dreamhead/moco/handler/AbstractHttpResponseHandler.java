package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.internal.SessionContext;

public abstract class AbstractHttpResponseHandler extends AbstractResponseHandler {
    protected abstract void doWriteToResponse(HttpRequest httpRequest, MutableHttpResponse httpResponse);

    @Override
    public final void writeToResponse(final SessionContext context) {
        Request request = context.getRequest();
        Response response = context.getResponse();

        if (HttpRequest.class.isInstance(request) && MutableHttpResponse.class.isInstance(response)) {
            HttpRequest httpRequest = HttpRequest.class.cast(request);
            MutableHttpResponse httpResponse = MutableHttpResponse.class.cast(response);
            doWriteToResponse(httpRequest, httpResponse);
        }
    }
}
