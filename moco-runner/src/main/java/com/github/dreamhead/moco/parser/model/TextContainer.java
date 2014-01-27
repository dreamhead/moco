package com.github.dreamhead.moco.parser.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

import static com.google.common.collect.ImmutableMap.of;

public class TextContainer {
    private final String text;
    private final String operation;
    private final ImmutableMap<String, Object> vars;

    public TextContainer(String text, String operation) {
        this.text = text;
        this.operation = operation;
        this.vars = of();
    }

    public TextContainer(String text, String operation, ImmutableMap<String, Object> vars) {
        this.text = text;
        this.operation = operation;
        this.vars = vars;
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

    public boolean hasVars() {
        return !this.vars.isEmpty();
    }

    public ImmutableMap<String, Object> getVars() {
        return vars;
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
