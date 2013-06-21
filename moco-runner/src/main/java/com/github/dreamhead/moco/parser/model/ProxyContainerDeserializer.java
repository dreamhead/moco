package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ProxyContainerDeserializer extends JsonDeserializer<ProxyContainer> {
    @Override
    public ProxyContainer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return new ProxyContainer(jp.getText().trim(), null);
        } else if (currentToken == JsonToken.START_OBJECT) {
            JsonToken jsonToken = jp.nextToken();
            if (jsonToken == JsonToken.FIELD_NAME) {
                return createFailoverProxy(jp);
            }
        }

        throw ctxt.mappingException(TextContainer.class, currentToken);
    }

    private ProxyContainer createFailoverProxy(JsonParser jp) throws IOException {
        Map<String, String> fields = newHashMap();
        fetchField(fields, jp);
        fetchField(fields, jp);
        return new ProxyContainer(fields.get("url"), fields.get("failover"));
    }

    private void fetchField(Map<String, String> fields, JsonParser jp) throws IOException {
        String fieldName = jp.getText().trim();
        jp.nextToken();
        String fieldValue = jp.getText().trim();
        jp.nextToken();
        fields.put(fieldName.toLowerCase(), fieldValue);
    }
}
