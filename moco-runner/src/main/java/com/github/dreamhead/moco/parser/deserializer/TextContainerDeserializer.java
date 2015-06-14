package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.io.IOException;

public class TextContainerDeserializer extends AbstractTextContainerDeserializer<TextContainer> {
    @Override
    public TextContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return text(jp);
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();
            return textContainer(jp, ctxt);
        }

        throw ctxt.mappingException(TextContainer.class, currentToken);
    }
}
