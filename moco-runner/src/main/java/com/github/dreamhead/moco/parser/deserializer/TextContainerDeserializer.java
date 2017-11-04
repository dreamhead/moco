package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.io.IOException;

public final class TextContainerDeserializer extends JsonDeserializer<TextContainer> {
    private TextContainerDeserializerHelper helper = new TextContainerDeserializerHelper();

    @Override
    public TextContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return helper.text(jp);
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();
            return helper.textContainer(jp, ctxt);
        }

        return (TextContainer) ctxt.handleUnexpectedToken(TextContainer.class, jp);
    }
}
