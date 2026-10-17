package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.github.dreamhead.moco.parser.model.ReplayContainer;
import com.github.dreamhead.moco.parser.model.ReplayModifierContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;

public class ReplayContainerDeserializer extends ValueDeserializer<ReplayContainer> {
    private TextContainerDeserializerHelper helper = new TextContainerDeserializerHelper();

    @Override
    public final ReplayContainer deserialize(final JsonParser p, final DeserializationContext ctxt) {
        JsonToken currentToken = p.currentToken();
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
    private static final class InternalReplayContainer {
        private String group;
        private TextContainer identifier;
        private ReplayModifierContainer modifier;
        private String tape;

        private ReplayContainer toContainer() {
            return new ReplayContainer(group, identifier, modifier, tape);
        }
    }
}
