package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.LatencyContainer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Iterators.get;

public class LatencyContainerDeserializer extends JsonDeserializer<LatencyContainer> {
    @Override
    public LatencyContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_NUMBER_INT) {
            return LatencyContainer.latency(jp.getLongValue());
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();
            InternalLatencyContainer container = get(jp.readValuesAs(InternalLatencyContainer.class), 0);
            return LatencyContainer.latencyWithUnit(container.duration,
                    TimeUnit.valueOf(container.unit.toUpperCase() + 'S'));
        }

        throw ctxt.mappingException(LatencyContainer.class, currentToken);
    }

    private static class InternalLatencyContainer {
        public long duration;
        public String unit;
    }
}
