package com.github.dreamhead.moco.parser.model;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

public class TextContainer {
    private static final String TEMPLATE_NAME = "template";
    private String text;
    private String operation;
    private ImmutableMap<String, TextContainer> props;

    public boolean isRawText() {
        return this.operation == null;
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

    public ImmutableMap<String, TextContainer> getProps() {
        return props;
    }

    public static boolean isForTemplate(String operation) {
        return TEMPLATE_NAME.equalsIgnoreCase(operation);
    }

    public boolean isForTemplate() {
        return isForTemplate(this.operation);
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
        private ImmutableMap<String, TextContainer> props;

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public Builder withOperation(String operation) {
            this.operation = operation;
            return this;
        }

        public Builder withProps(ImmutableMap<String, TextContainer> props) {
            this.props = props;
            return this;
        }

        public TextContainer build() {
            TextContainer container = new TextContainer();
            container.text = text;
            container.operation = operation;
            container.props = (props != null) ? props : ImmutableMap.<String, TextContainer>of();
            return container;
        }
    }
}
