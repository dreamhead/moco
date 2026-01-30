package com.github.dreamhead.moco.sse;

import com.github.dreamhead.moco.handler.SseResponseHandler;
import com.github.dreamhead.moco.util.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

public class SseResponse {
    private final List<SseEvent> events = Lists.newArrayList();
    private SseEvent.Builder currentEventBuilder;

    SseResponse() {
    }

    public static SseResponse create() {
        return new SseResponse();
    }

    public SseResponse event(final String eventName, final String first, final String... rest) {
        if (currentEventBuilder != null) {
            events.add(currentEventBuilder.build());
        }

        List<String> data = Iterables.asIterable(first, rest);
        currentEventBuilder = SseEvent.event(eventName, data.toArray(new String[0]));
        return this;
    }

    public SseResponse data(final String first, final String... rest) {
        if (currentEventBuilder != null) {
            events.add(currentEventBuilder.build());
        }

        List<String> data = Iterables.asIterable(first, rest);
        currentEventBuilder = SseEvent.data(data.toArray(new String[0]));
        return this;
    }

    public SseResponse id(final String id) {
        if (currentEventBuilder == null) {
            currentEventBuilder = SseEvent.id(id);
        } else {
            currentEventBuilder.id(id);
        }
        return this;
    }

    public SseResponse retry(final int retry) {
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
