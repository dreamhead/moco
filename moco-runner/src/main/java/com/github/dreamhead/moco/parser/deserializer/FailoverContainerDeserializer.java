package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.FailoverContainer;

import java.io.IOException;

public final class FailoverContainerDeserializer extends JsonDeserializer<FailoverContainer> {
    @Override
    public FailoverContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return FailoverContainer.builder().withFile(jp.getText()).build();
        }

        if (currentToken == JsonToken.START_OBJECT) {
            InternalFailoverContainer container = jp.readValueAs(InternalFailoverContainer.class);
            return container.toFailoverContainer();
        }

        return (FailoverContainer) ctxt.handleUnexpectedToken(FailoverContainer.class, jp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class InternalFailoverContainer {
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
