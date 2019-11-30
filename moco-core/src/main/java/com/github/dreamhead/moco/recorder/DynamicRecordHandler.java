package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.net.MediaType;

public class DynamicRecordHandler extends AbstractHttpContentResponseHandler implements RecordHandler {
    private RecorderRegistry registry;
    private ContentResource name;

    public DynamicRecordHandler(final RecorderRegistry recorderRegistry,
                                final ContentResource name) {
        this.registry = recorderRegistry;
        this.name = name;
    }

    @Override
    protected MessageContent responseContent(final HttpRequest httpRequest) {
        RequestRecorder recorder = getRequestRecorder(httpRequest);
        recorder.record(httpRequest);
        return MessageContent.content().build();
    }

    private RequestRecorder getRequestRecorder(final HttpRequest httpRequest) {
        return registry.recorderOf(this.name.readFor(httpRequest).toString());
    }

    @Override
    protected MediaType getContentType(HttpRequest request) {
        return MediaType.PLAIN_TEXT_UTF_8;
    }
}
