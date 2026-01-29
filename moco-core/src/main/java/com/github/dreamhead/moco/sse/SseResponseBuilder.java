package com.github.dreamhead.moco.sse;

import com.github.dreamhead.moco.handler.SseResponseHandler;
import com.github.dreamhead.moco.util.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

public class SseResponseBuilder {
    private final List<SseEvent> events = Lists.newArrayList();
    private SseEvent.Builder currentEventBuilder;

    public SseResponseBuilder() {
    }

    public static SseResponseBuilder builder() {
        return new SseResponseBuilder();
    }

    public SseResponseBuilder event(final String eventName, final String first, final String... rest) {
        if (currentEventBuilder != null) {
            events.add(currentEventBuilder.build());
        }

        List<String> data = Iterables.asIterable(first, rest);
        currentEventBuilder = SseEvent.event(eventName, data.toArray(new String[0]));
        return this;
    }

    public SseResponseBuilder data(final String first, final String... rest) {
        if (currentEventBuilder != null) {
            events.add(currentEventBuilder.build());
        }

        List<String> data = Iterables.asIterable(first, rest);
        currentEventBuilder = SseEvent.data(data.toArray(new String[0]));
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
