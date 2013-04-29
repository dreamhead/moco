package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TextContainerDeserializer extends JsonDeserializer<TextContainer> {
    @Override
    public TextContainer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return new TextContainer(jp.getText().trim(), null);
        } else if (currentToken == JsonToken.START_OBJECT) {
            JsonToken jsonToken = jp.nextToken();
            if (jsonToken == JsonToken.FIELD_NAME) {
                String operation = jp.getText().trim();
                jp.nextToken();
                String text = jp.getText().trim();
                jp.nextToken();
                return new TextContainer(text, operation);
            }
        }

        throw ctxt.mappingException(TextContainer.class, currentToken);
    }
}
