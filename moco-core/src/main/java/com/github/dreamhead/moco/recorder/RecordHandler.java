package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.google.common.net.MediaType;

public class RecordHandler extends AbstractHttpContentResponseHandler {
    private RequestRecorder recorder;

    public RecordHandler(final RequestRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    protected MessageContent responseContent(final HttpRequest httpRequest) {
        recorder.record(httpRequest);
        return MessageContent.content().build();
    }

    @Override
    protected MediaType getContentType(HttpRequest request) {
        return MediaType.PLAIN_TEXT_UTF_8;
    }
}
