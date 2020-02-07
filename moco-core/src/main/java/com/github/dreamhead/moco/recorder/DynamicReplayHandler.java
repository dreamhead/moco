package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.google.common.net.MediaType;

public class DynamicReplayHandler extends AbstractHttpContentResponseHandler {
    private RecorderRegistry registry;
    private RecorderIdentifier identifier;
    private RecorderModifier modifier;

    public DynamicReplayHandler(final RecorderConfigurations configurations) {
        this.registry = configurations.getRecorderRegistry();
        this.identifier = configurations.getIdentifier();
        this.modifier = configurations.getModifier();
    }

    @Override
    protected final MessageContent responseContent(final HttpRequest request) {
        HttpRequest recordedRequest = getRequiredRecordedRequest(request);
        return modifier.getMessageContent(recordedRequest);
    }

    @Override
    protected final MediaType getContentType(final HttpRequest request) {
        HttpRequest recordedRequest = getRecordedRequest(request);
        return this.modifier.getContentType(recordedRequest);
    }

    private HttpRequest getRequiredRecordedRequest(final HttpRequest request) {
        HttpRequest recordedRequest = getRecordedRequest(request);
        if (recordedRequest == null) {
            throw new IllegalArgumentException("No recorded request for [" + identifier + "]");
        }
        return recordedRequest;
    }

    protected final HttpRequest getRecordedRequest(final HttpRequest request) {
        String name = this.identifier.getIdentifier(request);
        RequestRecorder recorder = registry.recorderOf(name);
        return recorder.getRequest();
    }
}
