package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.net.MediaType;

public class DynamicRecordHandler extends AbstractHttpContentResponseHandler implements RecordHandler {
    private RecorderRegistry registry;
    private ContentResource identifier;

    public DynamicRecordHandler(final RecorderRegistry recorderRegistry,
                                final ContentResource identifier) {
        this.registry = recorderRegistry;
        this.identifier = identifier;
    }

    @Override
    protected MessageContent responseContent(final HttpRequest httpRequest) {
        RequestRecorder recorder = getRequestRecorder(httpRequest);
        recorder.record(httpRequest);
        return MessageContent.content().build();
    }

    private RequestRecorder getRequestRecorder(final HttpRequest httpRequest) {
        return registry.recorderOf(this.identifier.readFor(httpRequest).toString());
    }

    @Override
    protected MediaType getContentType(final HttpRequest request) {
        return MediaType.PLAIN_TEXT_UTF_8;
    }
}
