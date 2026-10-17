package com.github.dreamhead.moco.parser.deserializer;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.github.dreamhead.moco.parser.model.ReplayContainer;
import com.github.dreamhead.moco.parser.model.ReplayModifierContainer;
import com.github.dreamhead.moco.parser.model.ResponseSetting;

import static com.github.dreamhead.moco.util.Strings.strip;

public class ReplayModifierContainerDeserializer extends ValueDeserializer<ReplayModifierContainer> {
    @Override
    public final ReplayModifierContainer deserialize(final JsonParser p, final DeserializationContext ctxt)  {
        JsonToken currentToken = p.currentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return new ReplayModifierContainer(strip(p.getValueAsString()));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            p.nextToken();
            ResponseSetting setting = p.readValueAs(ResponseSetting.class);
            return new ReplayModifierContainer(setting);
        }

        return (ReplayModifierContainer) ctxt.handleUnexpectedToken(ReplayContainer.class, p);
    }
}
