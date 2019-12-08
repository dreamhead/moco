package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.github.dreamhead.moco.mount.AbstractHttpContentResponseHandler;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.net.MediaType;
import org.apache.http.HttpHeaders;

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
        HttpRequest recordedRequest = getRecordedRequest(request);
        if (recordedRequest == null) {
            throw new IllegalArgumentException("No recorded request for [" + name + "]");
        }

        return replayModifier.readFor(recordedRequest);
    }

    private HttpRequest getRecordedRequest(final HttpRequest request) {
        String name = this.name.readFor(request).toString();
        RequestRecorder recorder = registry.recorderOf(name);
        return recorder.getRequest();
    }

    @Override
    protected MediaType getContentType(final HttpRequest request) {
        HttpRequest recordedRequest = getRecordedRequest(request);
        if (recordedRequest == null) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }

        String header = recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE);
        if (header == null) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }
        try {
            return MediaType.parse(header);
        } catch (Exception e) {
            return MediaType.PLAIN_TEXT_UTF_8;
        }
    }
}
