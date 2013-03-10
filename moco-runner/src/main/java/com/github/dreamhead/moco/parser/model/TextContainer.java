package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;

public class TextContainer {
    private String text;
    private String operation;

    public TextContainer(String text, String operation) {
        this.text = text;
        this.operation = operation;
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .add("text", text)
                .add("operation", operation)
                .toString();
    }
}
