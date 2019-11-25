package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.net.MediaType;
import org.apache.http.HttpHeaders;

import java.awt.*;

public class RequestRecorder {
    private HttpRequest httpRequest;

    public void record(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public MessageContent getContent() {
        return httpRequest.getContent();
    }

    public MediaType getContentType() {
        String header = httpRequest.getHeader(HttpHeaders.CONTENT_TYPE);
        if (header == null) {
            return null;
        }
        try {
            return MediaType.parse(header);
        } catch (Exception e) {
            return null;
        }
    }
}
