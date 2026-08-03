package com.github.dreamhead.moco.sse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamhead.moco.util.Preconditions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public record SseEvent(
        String id,
        String event,
        List<String> data,
        Integer retry,
        long delay
) {
    public SseEvent {
        if (retry != null) {
            Preconditions.checkArgument(retry > 0, "Retry must be positive");
        }
    }

    @JsonCreator
    public static SseEvent newEvent(@JsonProperty("id") final String id,
                                    @JsonProperty("event") final String event,
                                    @JsonProperty("data") final List<String> data,
                                    @JsonProperty("retry") final Integer retry,
                                    @JsonProperty("delay") final long delay) {
        return new SseEvent(id, event, data, retry, delay);
    }

    public static SseEvent event(final String name, final List<String> data) {
        return new SseEvent(null, name, data, null, 0);
    }

    public static SseEvent data(final List<String> data) {
        return new SseEvent(null, null, data, null, 0);
    }

    public SseEvent id(final String id) {
        requireNonNull(id, "Event ID should not be null");
        return new SseEvent(id, this.event, this.data, this.retry, this.delay);
    }

    public SseEvent retry(final int retry) {
        Preconditions.checkArgument(retry > 0, "Retry must be positive");
        return new SseEvent(this.id, this.event, this.data, retry, this.delay);
    }

    public SseEvent delay(final long delay) {
        Preconditions.checkArgument(delay > 0, "Delay must be positive");
        return new SseEvent(this.id, this.event, this.data, this.retry, delay);
    }

    public SseEvent delay(final long duration, final TimeUnit unit) {
        Preconditions.checkArgument(duration > 0, "Delay must be positive");
        requireNonNull(unit, "Time unit should not be null");
        return new SseEvent(this.id, this.event, this.data, this.retry,
                unit.toMillis(duration));
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
}
