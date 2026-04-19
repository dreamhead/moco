package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.FileContainer;
import com.github.dreamhead.moco.parser.model.SseContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.github.dreamhead.moco.sse.SseEvent;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class SseContainerDeserializer extends JsonDeserializer<SseContainer> {
    @Override
    public SseContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();

        if (currentToken == JsonToken.START_ARRAY) {
            return SseContainer.fromEvents(parseEvents(jp));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            return parseObject(jp);
        }

        return (SseContainer) ctxt.handleUnexpectedToken(SseContainer.class, jp);
    }

    private SseContainer parseObject(final JsonParser jp) throws IOException {
        SseObjectVar var = jp.readValueAs(SseObjectVar.class);
        Delay delay = Delay.from(var.delay);

        if (var.file != null) {
            return SseContainer.fromFile(FileContainer.asFileContainer(var.file), delay.duration, delay.unit);
        }

        if (var.events != null) {
            ImmutableList.Builder<SseEvent> builder = ImmutableList.builder();
            for (EventVar eventVar : var.events) {
                builder.add(eventVar.toEvent());
            }
            return SseContainer.fromEvents(builder.build(), delay.duration, delay.unit);
        }

        throw new IOException("Invalid SSE configuration: expected 'file' or 'events'");
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class SseObjectVar {
        private TextContainer file;
        private Object delay;
        private List<EventVar> events;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class DelayVar {
        private long duration;
        private String unit;
    }

    private static final class Delay {
        private final long duration;
        private final TimeUnit unit;

        private Delay(final long duration, final TimeUnit unit) {
            this.duration = duration;
            this.unit = unit;
        }

        @SuppressWarnings("unchecked")
        static Delay from(final Object delay) {
            if (delay == null) {
                return new Delay(0, TimeUnit.MILLISECONDS);
            }

            if (delay instanceof Number) {
                return new Delay(((Number) delay).longValue(), TimeUnit.MILLISECONDS);
            }

            if (delay instanceof java.util.Map) {
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) delay;
                long duration = ((Number) map.get("duration")).longValue();
                String unit = (String) map.get("unit");
                TimeUnit timeUnit = unit != null
                        ? TimeUnit.valueOf(unit.toUpperCase() + 'S')
                        : TimeUnit.MILLISECONDS;
                return new Delay(duration, timeUnit);
            }

            throw new IllegalArgumentException("Invalid delay format");
        }
    }

    private List<SseEvent> parseEvents(final JsonParser jp) throws IOException {
        ImmutableList.Builder<SseEvent> builder = ImmutableList.builder();
        while (jp.nextToken() != JsonToken.END_ARRAY) {
            builder.add(parseEvent(jp));
        }
        return builder.build();
    }

    private SseEvent parseEvent(final JsonParser jp) throws IOException {
        EventVar eventVar = jp.readValueAs(EventVar.class);
        return eventVar.toEvent();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class EventVar {
        private String event;
        private String data;
        private String id;
        private Integer retry;
        private Object delay;

        public SseEvent toEvent() {
            SseEvent e;
            if (event != null) {
                e = SseEvent.event(event, ImmutableList.of(data));
            } else {
                e = SseEvent.data(ImmutableList.of(data));
            }

            if (id != null) {
                e = e.id(id);
            }
            if (retry != null) {
                e = e.retry(retry);
            }
            Delay d = Delay.from(delay);
            if (d.duration > 0) {
                e = e.delay(d.duration, d.unit);
            }
            return e;
        }
    }
}
