package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.net.HttpHeaders;

public abstract class AbstractContentResponseHandler extends AbstractResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    protected abstract String responseContent(final HttpRequest request);

    @Override
    public void writeToResponse(final SessionContext context) {
        Request request = context.getRequest();
        Response response = context.getResponse();

        if (HttpRequest.class.isInstance(request) && MutableHttpResponse.class.isInstance(response)) {
            HttpRequest httpRequest = HttpRequest.class.cast(request);
            MutableHttpResponse httpResponse = MutableHttpResponse.class.cast(response);
            doWriteToResponse(httpRequest, httpResponse);
        }
    }

    private void doWriteToResponse(HttpRequest httpRequest, MutableHttpResponse httpResponse) {
        String content = responseContent(httpRequest);
        httpResponse.setContent(content);
        httpResponse.addHeader(HttpHeaders.CONTENT_LENGTH, content.getBytes().length);

        if (!detector.hasContentType(httpResponse)) {
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, getContentType(httpRequest));
        }
    }

    protected String getContentType(final HttpRequest request) {
        return "text/html; charset=UTF-8";
    }
}
