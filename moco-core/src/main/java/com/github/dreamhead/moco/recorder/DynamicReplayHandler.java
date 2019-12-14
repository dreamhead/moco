package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.resource.ContentResource;

public class DynamicReplayHandler extends AbstractReplayHandler {
    private RecorderRegistry registry;
    private ContentResource name;
    private ContentResource replayModifier;

    public DynamicReplayHandler(final RecorderRegistry registry,
                                final ContentResource name,
                                final ContentResource replayModifier) {
        this.registry = registry;
        this.name = name;
        this.replayModifier = replayModifier;
    }

    @Override
    protected final MessageContent responseContent(final HttpRequest request) {
        HttpRequest recordedRequest = getRecordedRequest(request);
        if (recordedRequest == null) {
            throw new IllegalArgumentException("No recorded request for [" + name + "]");
        }

        return replayModifier.readFor(recordedRequest);
    }

    protected final HttpRequest getRecordedRequest(final HttpRequest request) {
        String name = this.name.readFor(request).toString();
        RequestRecorder recorder = registry.recorderOf(name);
        return recorder.getRequest();
    }
}
