package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.FailoverContainer;
import com.github.dreamhead.moco.parser.model.ProxyContainer;

import java.io.IOException;

public class FailoverContainerDeserializer extends JsonDeserializer<FailoverContainer> {
    @Override
    public FailoverContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return new FailoverContainer(jp.getText());
        }

        return (FailoverContainer) ctxt.handleUnexpectedToken(ProxyContainer.class, jp);
    }
}
