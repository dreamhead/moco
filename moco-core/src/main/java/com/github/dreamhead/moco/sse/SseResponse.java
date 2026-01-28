package com.github.dreamhead.moco.sse;

import com.github.dreamhead.moco.util.Iterables;

import java.util.List;

public class SseResponse {
    public SseResponseBuilder event(final String eventName, final String first, final String... rest) {
        List<String> data = Iterables.asIterable(first, rest);
        return new SseResponseBuilder().event(eventName, data.toArray(new String[0]));
    }

    public SseResponseBuilder data(final String first, final String... rest) {
        List<String> data = Iterables.asIterable(first, rest);
        return new SseResponseBuilder().data(data.toArray(new String[0]));
    }
}
