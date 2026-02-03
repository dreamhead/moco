package com.github.dreamhead.moco.sse;

import com.google.common.base.MoreObjects;
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
        Preconditions.checkNotNull(data, "Event data cannot be null");
        Builder builder = new Builder();
        builder.data(data);
        return builder;
    }

    public static Builder event(final String event, final String... data) {
        Preconditions.checkNotNull(event, "Event name cannot be null");
        Builder builder = new Builder();
        builder.event(event);
        builder.data(data);
        return builder;
    }

    public static Builder id(final String id) {
        Preconditions.checkNotNull(id, "Event ID cannot be null");
        Builder builder = new Builder();
        builder.id(id);
        return builder;
    }

    public static Builder retry(final int retry) {
        Preconditions.checkArgument(retry > 0, "Retry must be positive");
        Builder builder = new Builder();
        builder.retry(retry);
        return builder;
    }

    public String toEventString() {
        StringBuilder sb = new StringBuilder();

        sb.append(toLine("id", id));
        sb.append(toLine("event", event));
        sb.append(toLine("retry", retry));

        for (String line : data) {
            sb.append(toLine("data", line));
        }

        sb.append("\n");

        return sb.toString();
    }

    private String toLine(final String key, final Object value) {
        if (value != null) {
            return key + ": " + value + "\n";
        }
        return "";
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
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .omitEmptyValues()
                .add("id", id)
                .add("event", event)
                .add("retry", retry)
                .add("data", data)
                .toString();
    }

    public static class Builder {
        private String id;
        private String event;
        private Integer retry;
        private ImmutableList<String> data;

        private Builder() {
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
