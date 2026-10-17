package com.github.dreamhead.moco.parser.deserializer;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.github.dreamhead.moco.parser.model.TextContainer;

public final class TextContainerDeserializer extends ValueDeserializer<TextContainer> {
    private TextContainerDeserializerHelper helper = new TextContainerDeserializerHelper();

    @Override
    public TextContainer deserialize(final JsonParser jp, final DeserializationContext ctxt)  {
        JsonToken currentToken = jp.currentToken();
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
