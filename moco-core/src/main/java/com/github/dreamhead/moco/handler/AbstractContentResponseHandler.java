package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.google.common.net.HttpHeaders;

public abstract class AbstractContentResponseHandler extends AbstractHttpResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    protected abstract String responseContent(final HttpRequest request);

    @Override
    protected void doWriteToResponse(HttpRequest httpRequest, MutableHttpResponse httpResponse) {
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
