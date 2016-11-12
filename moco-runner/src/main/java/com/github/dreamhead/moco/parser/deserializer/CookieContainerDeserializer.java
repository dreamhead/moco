package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.CookieContainer;
import com.github.dreamhead.moco.parser.model.LatencyContainer;

import java.io.IOException;

import static com.google.common.collect.Iterators.get;

public class CookieContainerDeserializer extends JsonDeserializer<CookieContainer> {
    @Override
    public CookieContainer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return CookieContainer.newContainer(jp.getText());
        }

        if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();
            InternalCookieContainer container = get(jp.readValuesAs(InternalCookieContainer.class), 0);
            return container.toContainer();
        }

        return (CookieContainer) ctxt.handleUnexpectedToken(CookieContainer.class, jp);
    }

    public static class InternalCookieContainer {
        public String value;
        public String path;
        public String domain;
        public LatencyContainer maxAge;
        public boolean secure;
        public boolean httpOnly;
        public String template;

        public CookieContainer toContainer() {
            return CookieContainer.newContainer(value, path, domain, maxAge, secure, httpOnly, template);
        }
    }
}
