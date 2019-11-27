package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.net.MediaType;

public class DynamicReplayHandler extends AbstractHttpContentResponseHandler implements ReplayHandler {
    private RecorderRegistry registry;
    private ContentResource name;

    public DynamicReplayHandler(final RecorderRegistry registry, final ContentResource name) {
        this.registry = registry;
        this.name = name;
    }

    @Override
    protected MessageContent responseContent(final HttpRequest request) {
        RequestRecorder recorder = registry.of(this.name.readFor(request).toString());
        return recorder.getContent();
    }

    @Override
    protected MediaType getContentType(final HttpRequest request) {
        RequestRecorder recorder = registry.of(this.name.readFor(request).toString());
        return recorder.getContentType();
    }
}
