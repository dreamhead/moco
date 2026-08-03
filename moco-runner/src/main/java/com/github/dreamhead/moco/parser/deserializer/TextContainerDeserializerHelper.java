package com.github.dreamhead.moco.parser.deserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import com.github.dreamhead.moco.parser.model.TextContainer;
import com.github.dreamhead.moco.util.Maps;

import java.io.IOException;
import java.util.Map;

import static com.github.dreamhead.moco.parser.model.TextContainer.builder;
import static com.github.dreamhead.moco.parser.model.TextContainer.getTemplateName;
import static com.github.dreamhead.moco.parser.model.TextContainer.isForTemplate;
import static com.github.dreamhead.moco.util.Strings.strip;
import static com.github.dreamhead.moco.util.Maps.orderedCopyOf;

public final class TextContainerDeserializerHelper {
    private static final Map<String, String> NAMES = Map.of(
            "json_path", "jsonPaths",
            "xpath", "xpaths",
            "header", "headers",
            "cookie", "cookies",
            "form", "forms");

    public TextContainer textContainer(final JsonParser jp, final DeserializationContext ctxt)  {
        JsonToken currentToken = jp.currentToken();
        if (currentToken == JsonToken.PROPERTY_NAME) {
            String operation = strip(jp.getValueAsString());

            JsonToken token = jp.nextToken();
            if (isForTemplate(operation) && token == JsonToken.START_OBJECT) {
                Template template = jp.readValueAs(Template.class);
                jp.nextToken();
                return template.template();
            }

            if (token == JsonToken.VALUE_STRING) {
                String text = strip(jp.getValueAsString());
                jp.nextToken();
                return builder().withOperation(operation).withText(text).build();
            }
        }

        return (TextContainer) ctxt.handleUnexpectedToken(TextContainer.class, jp);
    }

    protected TextContainer text(final JsonParser jp)  {
        return builder().withText(strip(jp.getValueAsString())).build();
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Template {
        private String with;
        private Map<String, TextContainer> vars;

        private Map<String, TextContainer> toTemplateVars() {
            return vars.entrySet().stream()
                    .collect(Maps.toOrderedMap(Map.Entry::getKey, e -> toLocalContainer(e.getValue())));
        }

        private TextContainer toLocalContainer(final TextContainer container) {
            if (container.isRawText()) {
                return container;
            }

            return toLocal(container);
        }

        private TextContainer toLocal(final TextContainer container) {
            String name = NAMES.get(container.getOperation());
            if (name == null) {
                return container;
            }
            return builder().withOperation(name).withText(container.getText()).withProps(container.getProps()).build();
        }

        public TextContainer template() {
            return builder().withOperation(getTemplateName()).withText(with).withProps(toTemplateVars()).build();
        }
    }
}
