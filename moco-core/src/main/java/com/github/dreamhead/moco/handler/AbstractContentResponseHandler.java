package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.net.HttpHeaders;

public abstract class AbstractContentResponseHandler extends AbstractResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    protected abstract String responseContent(final HttpRequest request);

    @Override
    public void writeToResponse(final SessionContext context) {
        Request request = context.getRequest();
        MutableHttpResponse httpResponse = context.getHttpResponse();

        if (HttpRequest.class.isInstance(request)) {
            doWriteToResponse(request, httpResponse);
        }
    }

    private void doWriteToResponse(Request request, MutableHttpResponse httpResponse) {
        HttpRequest httpRequest = HttpRequest.class.cast(request);
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
