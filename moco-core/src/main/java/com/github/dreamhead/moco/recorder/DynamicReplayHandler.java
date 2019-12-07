package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.net.MediaType;

public class DynamicReplayHandler extends AbstractHttpContentResponseHandler implements ReplayHandler {
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
    protected MessageContent responseContent(final HttpRequest request) {
        String name = this.name.readFor(request).toString();
        RequestRecorder recorder = registry.recorderOf(name);
        HttpRequest recordedRequest = recorder.getRequest();
        if (recordedRequest == null) {
            throw new IllegalArgumentException("No recorded request for [" + name + "]");
        }
        return replayModifier.readFor(recordedRequest);
    }

    @Override
    protected MediaType getContentType(final HttpRequest request) {
        RequestRecorder recorder = registry.recorderOf(this.name.readFor(request).toString());
        return recorder.getContentType();
    }
}
