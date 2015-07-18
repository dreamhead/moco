package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.net.HttpHeaders;

public abstract class AbstractContentResponseHandler extends AbstractResponseHandler {
    private final HeaderDetector detector = new HeaderDetector();

    protected abstract MessageContent responseContent(final Request request);

    @Override
    public void writeToResponse(final SessionContext context) {
        Request request = context.getRequest();
        Response response = context.getResponse();

        if (HttpRequest.class.isInstance(request) && MutableHttpResponse.class.isInstance(response)) {
            HttpRequest httpRequest = HttpRequest.class.cast(request);
            MutableHttpResponse httpResponse = MutableHttpResponse.class.cast(response);
            doWriteToResponse(httpRequest, httpResponse);
            return;
        }

        MutableResponse mutableResponse = MutableResponse.class.cast(response);
        mutableResponse.setContent(requireResponseContent(request));
    }

    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        MessageContent content = requireResponseContent(httpRequest);
        httpResponse.setContent(content);
        httpResponse.addHeader(HttpHeaders.CONTENT_LENGTH, content.getContent().length);

        if (!detector.hasContentType(httpResponse)) {
            httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, getContentType(httpRequest));
        }
    }

    private MessageContent requireResponseContent(final Request request) {
        MessageContent content = responseContent(request);
        if (content == null) {
            throw new IllegalStateException("Message content is expected. Please make sure responseContent method has been implemented correctly");
        }
        return content;
    }

    protected String getContentType(final HttpRequest request) {
        return FileContentType.DEFAULT_CONTENT_TYPE_WITH_CHARSET.toString();
    }
}
