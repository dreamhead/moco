package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.ReplayContainer;

import java.io.IOException;

import static com.github.dreamhead.moco.util.Strings.strip;

public class ReplayContainerDeserializer extends JsonDeserializer<ReplayContainer> {
    private TextContainerDeserializerHelper helper = new TextContainerDeserializerHelper();

    @Override
    public ReplayContainer deserialize(final JsonParser p, final DeserializationContext ctxt)
            throws IOException {
        JsonToken currentToken = p.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return new ReplayContainer(helper.text(p));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            p.nextToken();
            currentToken = p.getCurrentToken();
            if (currentToken == JsonToken.FIELD_NAME && "identifier".equalsIgnoreCase(strip(p.getText()))) {
                p.nextToken(); // FIELD_NAME
                p.nextToken(); // START_OBJECT
                ReplayContainer container = new ReplayContainer(helper.textContainer(p, ctxt));
                p.nextToken(); // END_OBJECT
                return container;
            }
        }

        return (ReplayContainer) ctxt.handleUnexpectedToken(ReplayContainer.class, p);
    }
}
