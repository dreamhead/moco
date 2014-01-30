package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.ProxyContainer;
import com.github.dreamhead.moco.parser.model.TextContainer;

import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ProxyContainerDeserializer extends JsonDeserializer<ProxyContainer> {
    @Override
    public ProxyContainer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return ProxyContainer.builder().withUrl(jp.getText().trim()).build();
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
        while (fetchField(fields, jp)) {}
        return ProxyContainer.builder()
                .withUrl(fields.get("url"))
                .withFailover(fields.get("failover"))
                .withFrom(fields.get("from"))
                .withTo(fields.get("to"))
                .withPlayback(fields.get("playback"))
                .build();
    }

    private boolean fetchField(Map<String, String> fields, JsonParser jp) throws IOException {
        String fieldName = jp.getText().trim();
        jp.nextToken();
        String fieldValue = jp.getText().trim();
        jp.nextToken();
        fields.put(fieldName.toLowerCase(), fieldValue);

        return jp.getCurrentToken() != JsonToken.END_OBJECT;
    }
}
