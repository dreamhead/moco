package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.net.MediaType;
import org.apache.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

public class RequestRecorder {
    private HttpRequest httpRequest;

    public void record(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public MessageContent getContent() {
        if (httpRequest != null) {
            return httpRequest.getContent();
        }

        return MessageContent.content().build();
    }

    public MediaType getContentType() {
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
