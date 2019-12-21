package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;

public class DynamicReplayHandler extends AbstractReplayHandler {
    private RecorderRegistry registry;
    private RecorderIdentifier identifier;
    private RecorderModifier modifier;
    private RecorderConfigurations configurations;

    public DynamicReplayHandler(final RecorderConfigurations configurations) {
        this.registry = configurations.getRecorderRegistry();
        this.identifier = configurations.getIdentifier();
        this.modifier = configurations.getModifier();
        this.configurations = configurations;
    }

    @Override
    protected final MessageContent responseContent(final HttpRequest request) {
        HttpRequest recordedRequest = getRecordedRequest(request);
        if (recordedRequest == null) {
            throw new IllegalArgumentException("No recorded request for [" + identifier + "]");
        }

        return modifier.getMessageContent(recordedRequest);
    }

    protected final HttpRequest getRecordedRequest(final HttpRequest request) {
        String name = this.identifier.getIdentifier(request);
        RequestRecorder recorder = registry.recorderOf(name);
        return recorder.getRequest();
    }
}
