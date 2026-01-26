package com.github.dreamhead.moco.sse;

public class SseResponse {
    public SseResponseBuilder event(final String eventName, final String... data) {
        return new SseResponseBuilder().event(eventName, data);
    }

    public SseResponseBuilder data(final String... data) {
        return new SseResponseBuilder().data(data);
    }
}
