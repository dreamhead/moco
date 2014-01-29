package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Maps.newHashMap;

public class TextContainerDeserializer extends JsonDeserializer<TextContainer> {
    @Override
    public TextContainer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            return text(jp);
        } else if (currentToken == JsonToken.START_OBJECT) {
            jp.nextToken();
            return textContainer(jp, ctxt);
        }

        throw ctxt.mappingException(TextContainer.class, currentToken);
    }

    private TextContainer textContainer(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.FIELD_NAME) {
            TextContainer.Builder builder = TextContainer.builder();
            String operation = jp.getText().trim();
            builder.withOperation(operation);
            JsonToken token = jp.nextToken();
            if (token == JsonToken.VALUE_STRING) {
                String text = jp.getText().trim();
                jp.nextToken();
                return builder.withText(text).build();
            }

            if ("template".equals(operation) && token == JsonToken.START_OBJECT) {
                jp.nextToken();
                return template(jp, ctxt, builder);
            }
        }

        throw ctxt.mappingException(TextContainer.class, jp.getCurrentToken());
    }

    private TextContainer template(JsonParser jp, DeserializationContext ctxt, TextContainer.Builder builder) throws IOException {
        String with = jp.getText().trim();
        if ("with".equals(with)) {
            jp.nextToken();
            builder.withText(jp.getText().trim());

            jp.nextToken();
            String vars = jp.getText().trim();

            if ("vars".equals(vars)) {
                JsonToken startTemplateVars = jp.nextToken();
                if (startTemplateVars == JsonToken.START_OBJECT) {
                    jp.nextToken();
                    ImmutableMap<String, Object> fields = getProps(jp);
                    jp.nextToken();
                    jp.nextToken();
                    return builder.withProps(copyOf(fields)).build();
                }
            }
        }

        throw ctxt.mappingException(TextContainer.class, jp.getCurrentToken());
    }

    private TextContainer text(JsonParser jp) throws IOException {
        return TextContainer.builder().withText(jp.getText().trim()).build();
    }

    private ImmutableMap<String, Object> getProps(JsonParser jp) throws IOException {
        Map<String, Object> fields = newHashMap();
        while (fetchField(fields, jp)) {}
        return copyOf(fields);
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
