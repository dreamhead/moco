package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;

public class StaticReplayHandler extends AbstractReplayHandler {
    private RequestRecorder recorder;

    public StaticReplayHandler(final RequestRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    protected final MessageContent responseContent(final HttpRequest httpRequest) {
        return recorder.getRequest().getContent();
    }

    @Override
    protected final HttpRequest getRecordedRequest(final HttpRequest request) {
        return recorder.getRequest();
    }
}

