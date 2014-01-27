package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Maps.newHashMap;

public class TextContainerDeserializer extends JsonDeserializer<TextContainer> {
    @Override
    public TextContainer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return new TextContainer(jp.getText().trim(), null);
        } else if (currentToken == JsonToken.START_OBJECT) {
            JsonToken jsonToken = jp.nextToken();
            if (jsonToken == JsonToken.FIELD_NAME) {
                String operation = jp.getText().trim();
                JsonToken token = jp.nextToken();
                if (token == JsonToken.VALUE_STRING) {
                    String text = jp.getText().trim();
                    jp.nextToken();
                    return new TextContainer(text, operation);
                }

                if (token == JsonToken.START_OBJECT) {
                    jp.nextToken();
                    String with = jp.getText().trim();
                    if ("with".equals(with)) {
                        jp.nextToken();
                        String template = jp.getText().trim();
                        jp.nextToken();
                        String vars = jp.getText().trim();
                        if ("vars".equals(vars)) {
                            JsonToken startTemplateVars = jp.nextToken();
                            if (startTemplateVars == JsonToken.START_OBJECT) {

                                jp.nextToken();
                                Map<String, Object> fields = newHashMap();
                                while (fetchField(fields, jp)) {}
                                jp.nextToken();
                                jp.nextToken();
                                return new TextContainer(template, operation, copyOf(fields));
                            }
                        }
                    }
                }
            }
        }

        throw ctxt.mappingException(TextContainer.class, currentToken);
    }

    private boolean fetchField(Map<String, Object> fields, JsonParser jp) throws IOException {
        String fieldName = jp.getText().trim();
        jp.nextToken();
        String fieldValue = jp.getText().trim();
        jp.nextToken();
        fields.put(fieldName.toLowerCase(), fieldValue);

        return jp.getCurrentToken() != JsonToken.END_OBJECT;
    }
}
