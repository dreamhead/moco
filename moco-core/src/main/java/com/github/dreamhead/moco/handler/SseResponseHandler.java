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

import java.util.concurrent.TimeUnit;

public class SseResponseHandler extends AbstractHttpResponseHandler {
    private static final MediaType SSE_CONTENT_TYPE = MediaType.create("text", "event-stream");

    private final Iterable<SseEvent> events;
    private long defaultDelay;

    public SseResponseHandler(final Iterable<SseEvent> events) {
        this.events = events;
    }

    public SseResponseHandler delay(final long delay) {
        this.defaultDelay = delay;
        return this;
    }

    public SseResponseHandler delay(final long duration, final TimeUnit unit) {
        this.defaultDelay = unit.toMillis(duration);
        return this;
    }

    @Override
    protected void doWriteToResponse(final HttpRequest httpRequest, final MutableHttpResponse httpResponse) {
        httpResponse.addHeader(HttpHeaders.CONTENT_TYPE, SSE_CONTENT_TYPE.toString());
        httpResponse.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        httpResponse.addHeader(HttpHeaders.CONNECTION, "keep-alive");
        httpResponse.addHeader("X-Accel-Buffering", "no");

        if (httpResponse instanceof DefaultMutableHttpResponse) {
            httpResponse.setSseEvents(eventsWithDelay(events));
        }
    }

    private Iterable<SseEvent> eventsWithDelay(final Iterable<SseEvent> events) {
        if (defaultDelay <= 0) {
            return events;
        }

        ImmutableList.Builder<SseEvent> builder = ImmutableList.builder();
        for (SseEvent event : events) {
            if (event.getDelay() > 0) {
                builder.add(event);
            } else {
                builder.add(event.delay(defaultDelay));
            }
        }
        return builder.build();
    }

    @Override
    public ResponseHandler doApply(final MocoConfig config) {
        return this;
    }
}
