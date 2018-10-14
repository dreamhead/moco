package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.CookieContainer;
import com.github.dreamhead.moco.parser.model.LatencyContainer;

import java.io.IOException;

import static com.github.dreamhead.moco.util.Strings.strip;

public final class CookieContainerDeserializer extends JsonDeserializer<CookieContainer> {
    @Override
    public CookieContainer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return CookieContainer.newContainer(strip(jp.getText()));
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
        private String template;

        public CookieContainer toContainer() {
            return CookieContainer.newContainer(value, path, domain, maxAge, secure, httpOnly, template);
        }
    }
}
