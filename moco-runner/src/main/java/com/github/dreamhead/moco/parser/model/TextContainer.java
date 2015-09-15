package com.github.dreamhead.moco.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.dreamhead.moco.parser.deserializer.TextContainerDeserializer;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@JsonDeserialize(using = TextContainerDeserializer.class)
public class TextContainer {
    private static final String TEMPLATE_NAME = "template";
    private String text;
    private String operation;
    private Map<String, TextContainer> props;

    protected TextContainer(final String text, final String operation,
                            final Map<String, TextContainer> props) {
        this.text = text;
        this.operation = operation;
        this.props = props;
    }

    public TextContainer() {
    }

    public boolean isRawText() {
        return this.operation == null && text != null;
    }

    public String getText() {
        return text;
    }

    public String getOperation() {
        return operation;
    }

    public boolean hasProperties() {
        return !this.props.isEmpty();
    }

    public Map<String, TextContainer> getProps() {
        return props;
    }

    public static boolean isForTemplate(final String operation) {
        return TEMPLATE_NAME.equalsIgnoreCase(operation);
    }

    public boolean isForTemplate() {
        return isForTemplate(this.operation);
    }

    public boolean isFileContainer() {
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("text", text)
                .add("operation", operation)
                .add("properties", props)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private String operation;
        private Map<String, TextContainer> props;

        public Builder withText(final String text) {
            this.text = text;
            return this;
        }

        public Builder withOperation(final String operation) {
            this.operation = operation;
            return this;
        }

        public Builder withProps(final Map<String, TextContainer> props) {
            this.props = props;
            return this;
        }

        public TextContainer build() {
            TextContainer container = new TextContainer();
            container.text = text;
            container.operation = operation;
            container.props = asProps(props);
            return container;
        }

        private Map<String, TextContainer> asProps(final Map<String, TextContainer> props) {
            if (props != null) {
                return props;
            }

            return ImmutableMap.of();
        }
    }
}
