package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.google.common.net.MediaType;

public class StaticReplayHandler extends AbstractHttpContentResponseHandler {
    private RequestRecorder recorder;

    public StaticReplayHandler(RequestRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    protected MessageContent responseContent(final HttpRequest httpRequest) {
        return recorder.getContent();
    }

    @Override
    protected MediaType getContentType(final HttpRequest request) {
        return recorder.getContentType();
    }
}

