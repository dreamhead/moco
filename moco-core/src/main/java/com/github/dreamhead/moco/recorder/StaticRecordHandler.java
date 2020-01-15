package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.google.common.net.MediaType;

public class StaticRecordHandler extends AbstractHttpContentResponseHandler
        implements RecordHandler {
    private RequestRecorder recorder;

    public StaticRecordHandler(final RequestRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    protected final MessageContent responseContent(final HttpRequest httpRequest) {
        recorder.record(httpRequest);
        return MessageContent.content().build();
    }

    @Override
    protected final MediaType getContentType(final HttpRequest request) {
        return MediaType.PLAIN_TEXT_UTF_8;
    }
}
