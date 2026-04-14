package com.github.dreamhead.moco.sse;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Objects;

public final class SseEvent {
    private final String id;
    private final String event;
    private final List<String> data;
    private final Integer retry;
    private final int delay;

    SseEvent(final String id, final String event, final List<String> data,
             final Integer retry, final int delay) {
        this.id = id;
        this.event = event;
        this.data = data;
        this.retry = retry;
        this.delay = delay;
    }

    public static SseEvent event(final String name, final List<String> data) {
        return new SseEvent(null, name, data, null, 0);
    }

    public static SseEvent data(final List<String> data) {
        return new SseEvent(null, null, data, null, 0);
    }

    public SseEvent id(final String id) {
        Preconditions.checkNotNull(id, "Event ID should not be null");
        return new SseEvent(id, this.event, this.data, this.retry, this.delay);
    }

    public SseEvent retry(final int retry) {
        Preconditions.checkArgument(retry > 0, "Retry must be positive");
        return new SseEvent(this.id, this.event, this.data, retry, this.delay);
    }

    public SseEvent delay(final int delay) {
        Preconditions.checkArgument(delay > 0, "Delay must be positive");
        return new SseEvent(this.id, this.event, this.data, this.retry, delay);
    }

    public int getDelay() {
        return delay;
    }

    public String toEventString() {
        StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        if (event != null) {
            sb.append("event: ").append(event).append('\n');
        }
        if (retry != null) {
            sb.append("retry: ").append(retry).append('\n');
        }
        for (String line : data) {
            sb.append("data: ").append(line).append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SseEvent sseEvent = (SseEvent) o;
        return delay == sseEvent.delay
                && Objects.equals(id, sseEvent.id)
                && Objects.equals(event, sseEvent.event)
                && Objects.equals(data, sseEvent.data)
                && Objects.equals(retry, sseEvent.retry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, data, retry, delay);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .omitEmptyValues()
                .add("id", id)
                .add("event", event)
                .add("data", data)
                .add("retry", retry)
                .add("delay", delay)
                .toString();
    }
}
