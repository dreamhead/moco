package com.github.dreamhead.moco.handler;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MutableHttpResponse;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.model.DefaultMutableHttpResponse;
import com.github.dreamhead.moco.sse.SseEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

public class SseResponseHandler extends AbstractHttpResponseHandler {
    private static final MediaType SSE_CONTENT_TYPE = MediaType.create("text", "event-stream");

    private final Iterable<SseEvent> events;

    public SseResponseHandler(final Iterable<SseEvent> events) {
        this.events = events;
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, SSE_CONTENT_TYPE.toString());
        httpResponse.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        httpResponse.addHeader(HttpHeaders.CONNECTION, "keep-alive");
        httpResponse.addHeader("X-Accel-Buffering", "no");

        if (httpResponse instanceof DefaultMutableHttpResponse) {
            ((DefaultMutableHttpResponse) httpResponse).setSseEvents(ImmutableList.copyOf(events));
        }
    }

    @Override
    public ResponseHandler doApply(final MocoConfig config) {
        return this;
    }
}
