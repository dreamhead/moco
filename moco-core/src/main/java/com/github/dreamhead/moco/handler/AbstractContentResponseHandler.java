package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.internal.SessionContext;
import com.google.common.net.HttpHeaders;

public abstract class AbstractContentResponseHandler extends AbstractResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    protected abstract String responseContent(final HttpRequest request);

    @Override
    public void writeToResponse(final SessionContext context) {
        String content = responseContent(context.getRequest());

        MutableHttpResponse httpResponse = context.getHttpResponse();
        httpResponse.setContent(content);

        httpResponse.addHeader(HttpHeaders.CONTENT_LENGTH, content.getBytes().length);

        if (!detector.hasContentType(httpResponse)) {
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, getContentType(context.getRequest()));
        }
    }

    protected String getContentType(final HttpRequest request) {
        return "text/html; charset=UTF-8";
    }
}
