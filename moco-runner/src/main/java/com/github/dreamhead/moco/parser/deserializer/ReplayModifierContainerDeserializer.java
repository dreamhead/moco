package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.ReplayContainer;
import com.github.dreamhead.moco.parser.model.ReplayModifierContainer;
import com.github.dreamhead.moco.parser.model.ResponseSetting;

import java.io.IOException;

import static com.github.dreamhead.moco.util.Strings.strip;

public class ReplayModifierContainerDeserializer extends JsonDeserializer<ReplayModifierContainer> {
    @Override
    public ReplayModifierContainer deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = p.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return new ReplayModifierContainer(strip(p.getText()));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            p.nextToken();
            ResponseSetting setting = p.readValueAs(ResponseSetting.class);
            return new ReplayModifierContainer(setting);
        }

        return (ReplayModifierContainer) ctxt.handleUnexpectedToken(ReplayContainer.class, p);
    }
}
