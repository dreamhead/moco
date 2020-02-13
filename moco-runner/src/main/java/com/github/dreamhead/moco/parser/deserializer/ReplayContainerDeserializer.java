package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.ReplayContainer;
import com.github.dreamhead.moco.parser.model.ReplayModifierContainer;
import com.github.dreamhead.moco.parser.model.ResponseSetting;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.io.IOException;

public class ReplayContainerDeserializer extends JsonDeserializer<ReplayContainer> {
    private TextContainerDeserializerHelper helper = new TextContainerDeserializerHelper();

    @Override
    public final ReplayContainer deserialize(final JsonParser p, final DeserializationContext ctxt)
            throws IOException {
        JsonToken currentToken = p.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return new ReplayContainer(null, helper.text(p), null, null);
        }

        if (currentToken == JsonToken.START_OBJECT) {
            p.nextToken();
            InternalReplayContainer value = p.readValueAs(InternalReplayContainer.class);
            return value.toContainer();
        }

        return (ReplayContainer) ctxt.handleUnexpectedToken(ReplayContainer.class, p);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class InternalReplayContainer {
        private String group;
        private TextContainer identifier;
        private ReplayModifierContainer modifier;
        private String tape;

        private ReplayContainer toContainer() {
            return new ReplayContainer(group, identifier, modifier, tape);
        }
    }
}
