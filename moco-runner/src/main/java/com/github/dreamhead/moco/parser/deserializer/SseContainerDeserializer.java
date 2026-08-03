package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.github.dreamhead.moco.parser.model.FileContainer;
import com.github.dreamhead.moco.parser.model.SseContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.github.dreamhead.moco.sse.SseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class SseContainerDeserializer extends ValueDeserializer<SseContainer> {
    @Override
    public SseContainer deserialize(final JsonParser jp, final DeserializationContext ctxt)  {
        JsonToken currentToken = jp.currentToken();

        if (currentToken == JsonToken.START_ARRAY) {
            return SseContainer.fromEvents(parseEvents(jp));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            return parseObject(jp);
        }

        return (SseContainer) ctxt.handleUnexpectedToken(SseContainer.class, jp);
    }

    private SseContainer parseObject(final JsonParser jp)  {
        SseObjectVar var = jp.readValueAs(SseObjectVar.class);
        Delay delay = Delay.from(var.delay);

        if (var.file != null) {
            return SseContainer.fromFile(FileContainer.asFileContainer(var.file), delay.duration, delay.unit);
        }

        if (var.events != null) {
            List<SseEvent> events = new ArrayList<>();
            for (EventVar eventVar : var.events) {
                events.add(eventVar.toEvent());
            }
            return SseContainer.fromEvents(List.copyOf(events), delay.duration, delay.unit);
        }

        throw new IllegalArgumentException("Invalid SSE configuration: expected 'file' or 'events'");
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

    private List<SseEvent> parseEvents(final JsonParser jp)  {
        List<SseEvent> events = new ArrayList<>();
        while (jp.nextToken() != JsonToken.END_ARRAY) {
            events.add(parseEvent(jp));
        }
        return List.copyOf(events);
    }

    private SseEvent parseEvent(final JsonParser jp)  {
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
                e = SseEvent.event(event, List.of(data));
            } else {
                e = SseEvent.data(List.of(data));
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
