package com.github.dreamhead.moco.sse;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public class SseEvent {
    private final String id;
    private final String event;
    private final Integer retry;
    private final List<String> data;

    private SseEvent(final String id, final String event, final Integer retry, final List<String> data) {
        this.id = id;
        this.event = event;
        this.retry = retry;
        this.data = data != null ? ImmutableList.copyOf(data) : ImmutableList.of();
    }

    public static Builder data(final String... data) {
        return new Builder(null, null, null, data != null ? ImmutableList.copyOf(data) : ImmutableList.of());
    }

    public static Builder event(final String event, final String... data) {
        Preconditions.checkNotNull(event, "Event name cannot be null");
        return new Builder(null, event, null, data != null ? ImmutableList.copyOf(data) : ImmutableList.of());
    }

    public static Builder id(final String id) {
        Preconditions.checkNotNull(id, "Event ID cannot be null");
        return new Builder(id, null, null, ImmutableList.of());
    }

    public static Builder retry(final int retry) {
        Preconditions.checkArgument(retry > 0, "Retry must be positive");
        return new Builder(null, null, retry, ImmutableList.of());
    }

    public String toEventString() {
        StringBuilder sb = new StringBuilder();

        if (id != null) {
            sb.append("id: ").append(id).append("\n");
        }

        if (event != null) {
            sb.append("event: ").append(event).append("\n");
        }

        if (retry != null) {
            sb.append("retry: ").append(retry).append("\n");
        }

        for (String line : data) {
            sb.append("data: ").append(line).append("\n");
        }

        sb.append("\n");

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
        return Objects.equals(id, sseEvent.id) &&
                Objects.equals(event, sseEvent.event) &&
                Objects.equals(retry, sseEvent.retry) &&
                Objects.equals(data, sseEvent.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, retry, data);
    }

    @Override
    public String toString() {
        return "SseEvent{" +
                "id='" + id + '\'' +
                ", event='" + event + '\'' +
                ", retry=" + retry +
                ", data=" + data +
                '}';
    }

    public static class Builder {
        private String id;
        private String event;
        private Integer retry;
        private ImmutableList<String> data;

        private Builder(final String id, final String event, final Integer retry, final ImmutableList<String> data) {
            this.id = id;
            this.event = event;
            this.retry = retry;
            this.data = data;
        }

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder event(final String event) {
            this.event = event;
            return this;
        }

        public Builder retry(final int retry) {
            this.retry = retry;
            return this;
        }

        public Builder data(final String... data) {
            this.data = data != null ? ImmutableList.copyOf(data) : ImmutableList.of();
            return this;
        }

        public SseEvent build() {
            return new SseEvent(id, event, retry, data);
        }
    }
}
