package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.github.dreamhead.moco.parser.model.FailoverContainer;

public final class FailoverContainerDeserializer extends ValueDeserializer<FailoverContainer> {
    @Override
    public FailoverContainer deserialize(final JsonParser jp, final DeserializationContext ctxt)  {
        JsonToken currentToken = jp.currentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return FailoverContainer.builder().withFile(jp.getValueAsString()).build();
        }

        if (currentToken == JsonToken.START_OBJECT) {
            InternalFailoverContainer container = jp.readValueAs(InternalFailoverContainer.class);
            return container.toFailoverContainer();
        }

        return (FailoverContainer) ctxt.handleUnexpectedToken(FailoverContainer.class, jp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static final class InternalFailoverContainer {
        private String file;
        private int[] status;

        public FailoverContainer toFailoverContainer() {
            return FailoverContainer.builder()
                    .withFile(file)
                    .withStatus(status)
                    .build();
        }
    }
}
