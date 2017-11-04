package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.LatencyContainer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class LatencyContainerDeserializer extends JsonDeserializer<LatencyContainer> {
    @Override
    public LatencyContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_NUMBER_INT) {
            return LatencyContainer.latency(jp.getLongValue());
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();
            InternalLatencyContainer container = jp.readValueAs(InternalLatencyContainer.class);
            return container.toLatencyContainer();
        }

        return (LatencyContainer) ctxt.handleUnexpectedToken(LatencyContainer.class, jp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class InternalLatencyContainer {
        private long duration;
        private String unit;

        private LatencyContainer toLatencyContainer() {
            return LatencyContainer.latencyWithUnit(duration,
                    TimeUnit.valueOf(unit.toUpperCase() + 'S'));
        }
    }
}
