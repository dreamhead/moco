package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

public class TextContainer {
    private String text;
    private String operation;
    private ImmutableMap<String, Object> props;

    public TextContainer() {
    }

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

    public ImmutableMap<String, Object> getProps() {
        return props;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
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
        private ImmutableMap<String, Object> props;

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public Builder withOperation(String operation) {
            this.operation = operation;
            return this;
        }

        public Builder withProps(ImmutableMap<String, Object> props) {
            this.props = props;
            return this;
        }

        public TextContainer build() {
            TextContainer container = new TextContainer();
            container.text = text;
            container.operation = operation;
            container.props = (props != null) ? props : ImmutableMap.<String, Object>of();
            return container;
        }

    }
}
