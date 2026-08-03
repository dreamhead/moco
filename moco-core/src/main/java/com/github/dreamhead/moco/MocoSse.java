package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.SseResponseHandler;
import com.github.dreamhead.moco.resource.Resource;
import com.github.dreamhead.moco.sse.SseEvent;
import com.github.dreamhead.moco.sse.SseEventParser;
import com.github.dreamhead.moco.util.Iterables;
import com.github.dreamhead.moco.util.Preconditions;
import com.google.common.base.Splitter;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class MocoSse {

    private MocoSse() {
    }

    public static SseResponseHandler sse(final SseEvent first, final SseEvent... rest) {
        requireNonNull(first, "SSE event should not be null");
        List<SseEvent> events = Iterables.asIterable(first, rest);
        return new SseResponseHandler(events);
    }

    public static SseResponseHandler sse(final Resource resource) {
        requireNonNull(resource, "Resource should not be null");
        Iterable<String> lines = Splitter.on('\n').split(resource.readFor((Request) null).toString());
        return new SseResponseHandler(new SseEventParser().parse(lines));
    }

    public static SseEvent event(final String name, final String data, final String... rest) {
        requireNonNull(name, "Event name should not be null");
        requireNonNull(data, "Data should not be null");
        return SseEvent.event(name, Iterables.asIterable(data, rest));
    }

    public static SseEvent data(final String data, final String... rest) {
        requireNonNull(data, "Data should not be null");
        return SseEvent.data(Iterables.asIterable(data, rest));
    }
}
