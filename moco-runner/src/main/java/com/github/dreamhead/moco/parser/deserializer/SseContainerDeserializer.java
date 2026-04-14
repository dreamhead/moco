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

public final class SseContainerDeserializer extends JsonDeserializer<SseContainer> {
    @Override
    public SseContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();

        if (currentToken == JsonToken.START_ARRAY) {
            return SseContainer.fromEvents(parseEvents(jp));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            return SseContainer.fromFile(parseFile(jp, ctxt));
        }

        return (SseContainer) ctxt.handleUnexpectedToken(SseContainer.class, jp);
    }

    private FileContainer parseFile(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        SseFileVar fileVar = jp.readValueAs(SseFileVar.class);
        return fileVar.toFileContainer();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class SseFileVar {
        private TextContainer file;

        public FileContainer toFileContainer() {
            return FileContainer.asFileContainer(file);
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
        private Integer delay;

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
            if (delay != null) {
                e = e.delay(delay);
            }
            return e;
        }
    }
}
