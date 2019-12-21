package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.google.common.net.MediaType;

public class DynamicRecordHandler extends AbstractHttpContentResponseHandler implements RecordHandler {
    private RecorderRegistry registry;
    private RecorderIdentifier identifier;
    private RecorderConfigurations configurations;

    public DynamicRecordHandler(final RecorderConfigurations configurations) {
        this.registry = configurations.getRecorderRegistry();
        this.identifier = configurations.getIdentifier();
        this.configurations = configurations;
    }

    @Override
    protected MessageContent responseContent(final HttpRequest httpRequest) {
        RequestRecorder recorder = getRequestRecorder(httpRequest);
        recorder.record(httpRequest);
        return MessageContent.content().build();
    }

    private RequestRecorder getRequestRecorder(final HttpRequest httpRequest) {
        return registry.recorderOf(this.identifier.getIdentifier(httpRequest));
    }

    @Override
    protected MediaType getContentType(final HttpRequest request) {
        return MediaType.PLAIN_TEXT_UTF_8;
    }
}
