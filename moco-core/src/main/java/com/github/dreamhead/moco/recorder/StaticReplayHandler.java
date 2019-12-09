package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.google.common.net.MediaType;
import org.apache.http.HttpHeaders;

public class StaticReplayHandler extends AbstractHttpContentResponseHandler {
    private RequestRecorder recorder;

    public StaticReplayHandler(RequestRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    protected MessageContent responseContent(final HttpRequest httpRequest) {
        return recorder.getRequest().getContent();
    }

    @Override
    protected MediaType getContentType(final HttpRequest request) {
        HttpRequest httpRequest = recorder.getRequest();

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

