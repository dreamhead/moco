package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static com.github.dreamhead.moco.parser.model.TextContainer.builder;
import static com.github.dreamhead.moco.parser.model.TextContainer.isForTemplate;
import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Maps.transformEntries;

public abstract class AbstractTextContainerDeserializer<T extends TextContainer> extends JsonDeserializer<T> {
    private final ImmutableMap<String, String> names = ImmutableMap.<String, String>builder()
        .put("json_path", "jsonPaths")
        .put("xpath", "xpaths")
        .put("header", "headers")
        .put("cookie", "cookies")
        .put("form", "forms")
        .build();

    protected TextContainer textContainer(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.FIELD_NAME) {
            TextContainer.Builder builder = builder();
            String operation = jp.getText().trim();
            builder.withOperation(operation);
            JsonToken token = jp.nextToken();
            if (token == JsonToken.VALUE_STRING) {
                String text = jp.getText().trim();
                jp.nextToken();
                return builder.withText(text).build();
            }

            if (isForTemplate(operation) && token == JsonToken.START_OBJECT) {
                return template(jp, builder);
            }
        }

        throw ctxt.mappingException(TextContainer.class, jp.getCurrentToken());
    }

    private TextContainer template(final JsonParser jp, final TextContainer.Builder builder) throws IOException {
        Iterator<Template> iterator = jp.readValuesAs(Template.class);
        Template template = Iterators.get(iterator, 0);
        jp.nextToken();
        return builder.withText(template.with).withProps(toTemplateVars(template)).build();
    }

    private ImmutableMap<String, TextContainer> toTemplateVars(final Template template) {
        return copyOf(transformEntries(template.vars, toLocalContainer()));
    }

    private Maps.EntryTransformer<String, TextContainer, TextContainer> toLocalContainer() {
        return new Maps.EntryTransformer<String, TextContainer, TextContainer>() {
            @Override
            public TextContainer transformEntry(final String key, final TextContainer container) {
                if (container.isRawText()) {
                    return container;
                }

                return toLocal(container);
            }
        };
    }

    private TextContainer toLocal(final TextContainer container) {
        String name = names.get(container.getOperation());
        if (name == null) {
            return container;
        }
        return builder().withOperation(name).withText(container.getText()).withProps(container.getProps()).build();
    }

    protected TextContainer text(final JsonParser jp) throws IOException {
        return builder().withText(jp.getText().trim()).build();
    }

    private static class Template {
        public String with;
        public Map<String, TextContainer> vars;
    }
}
