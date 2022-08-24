package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

public class InMemoryRequestRecorder implements RequestRecorder {
    private HttpRequest httpRequest;

    @Override
    public final void record(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public final HttpRequest getRequest() {
        return httpRequest;
    }

    public final MessageContent getContent() {
        if (httpRequest != null) {
            return httpRequest.getContent();
        }

        return MessageContent.content().build();
    }

    public final MediaType getContentType() {
        if (httpRequest == null) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }

        String header = httpRequest.getHeader(HttpHeaders.CONTENT_TYPE);
        if (header == null) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }
        try {
            return MediaType.parse(header);
        } catch (Exception e) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }
    }
}
