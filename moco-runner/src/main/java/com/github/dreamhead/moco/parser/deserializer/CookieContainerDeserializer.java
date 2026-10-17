package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.github.dreamhead.moco.parser.model.CookieContainer;
import com.github.dreamhead.moco.parser.model.LatencyContainer;

import static com.github.dreamhead.moco.util.Strings.strip;

public final class CookieContainerDeserializer extends ValueDeserializer<CookieContainer> {
    @Override
    public CookieContainer deserialize(final JsonParser jp, final DeserializationContext ctxt)  {
        JsonToken currentToken = jp.currentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return CookieContainer.newContainer(strip(jp.getValueAsString()));
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();
            InternalCookieContainer container = jp.readValueAs(InternalCookieContainer.class);
            return container.toContainer();
        }

        return (CookieContainer) ctxt.handleUnexpectedToken(CookieContainer.class, jp);
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static final class InternalCookieContainer {
        private String value;
        private String path;
        private String domain;
        private LatencyContainer maxAge;
        private boolean secure;
        private boolean httpOnly;
        private String sameSite;
        private String template;

        public CookieContainer toContainer() {
            return CookieContainer.newContainer(value, path, domain, maxAge, secure, httpOnly, sameSite, template);
        }
    }
}
