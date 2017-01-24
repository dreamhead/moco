package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
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

public class TextContainerDeserializerHelper {
    private static final ImmutableMap<String, String> NAMES = ImmutableMap.<String, String>builder()
        .put("json_path", "jsonPaths")
        .put("xpath", "xpaths")
        .put("header", "headers")
        .put("cookie", "cookies")
        .put("form", "forms")
        .build();

    public TextContainer textContainer(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.FIELD_NAME) {
            TextContainer.Builder builder = builder();
            String operation = jp.getText().trim();
            builder.withOperation(operation);
            JsonToken token = jp.nextToken();
            if (isForTemplate(operation) && token == JsonToken.START_OBJECT) {
                return template(jp, builder);
            }

            if (token == JsonToken.VALUE_STRING) {
                String text = jp.getText().trim();
                jp.nextToken();
                return builder.withText(text).build();
            }
        }

        return (TextContainer) ctxt.handleUnexpectedToken(TextContainer.class, jp);
    }

    private TextContainer template(final JsonParser jp, final TextContainer.Builder builder) throws IOException {
        Iterator<Template> iterator = jp.readValuesAs(Template.class);
        Template template = Iterators.get(iterator, 0);
        jp.nextToken();
        return builder.withText(template.with).withProps(template.toTemplateVars()).build();
    }

    protected TextContainer text(final JsonParser jp) throws IOException {
        return builder().withText(jp.getText().trim()).build();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Template {
        private String with;
        private Map<String, TextContainer> vars;

        private ImmutableMap<String, TextContainer> toTemplateVars() {
            return copyOf(transformEntries(vars, toLocalContainer()));
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
            String name = NAMES.get(container.getOperation());
            if (name == null) {
                return container;
            }
            return builder().withOperation(name).withText(container.getText()).withProps(container.getProps()).build();
        }
    }
}
