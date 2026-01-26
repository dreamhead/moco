package com.github.dreamhead.moco.sse;

import com.github.dreamhead.moco.handler.SseResponseHandler;
import com.google.common.collect.Lists;

import java.util.List;

public class SseResponseBuilder {
    private final List<SseEvent> events = Lists.newArrayList();
    private SseEvent.Builder currentEventBuilder;

    SseResponseBuilder() {
    }

    public SseResponseBuilder event(final String eventName, final String... data) {
        if (currentEventBuilder != null) {
            events.add(currentEventBuilder.build());
        }

        currentEventBuilder = SseEvent.event(eventName, data);
        return this;
    }

    public SseResponseBuilder data(final String... data) {
        if (currentEventBuilder != null) {
            events.add(currentEventBuilder.build());
        }
        currentEventBuilder = SseEvent.data(data);
        return this;
    }

    public SseResponseBuilder id(final String id) {
        if (currentEventBuilder == null) {
            currentEventBuilder = SseEvent.id(id);
        } else {
            currentEventBuilder.id(id);
        }
        return this;
    }

    public SseResponseBuilder retry(final int retry) {
        if (currentEventBuilder == null) {
            currentEventBuilder = SseEvent.retry(retry);
        } else {
            currentEventBuilder.retry(retry);
        }
        return this;
    }

    public SseResponseHandler end() {
        if (currentEventBuilder != null) {
            events.add(currentEventBuilder.build());
        }
        return new SseResponseHandler(events);
    }
}
