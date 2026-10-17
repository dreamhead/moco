package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.github.dreamhead.moco.parser.model.LatencyContainer;

import java.util.concurrent.TimeUnit;

public final class LatencyContainerDeserializer extends ValueDeserializer<LatencyContainer> {
    @Override
    public LatencyContainer deserialize(final JsonParser jp, final DeserializationContext ctxt)  {
        JsonToken currentToken = jp.currentToken();
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
    private static final class InternalLatencyContainer {
        private long duration;
        private String unit;

        private LatencyContainer toLatencyContainer() {
            return LatencyContainer.latencyWithUnit(duration,
                    TimeUnit.valueOf(unit.toUpperCase() + 'S'));
        }
    }
}
