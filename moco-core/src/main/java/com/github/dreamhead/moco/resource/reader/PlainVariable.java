package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;

public class PlainVariable implements Variable {
    private Object text;

    public PlainVariable(final Object text) {
        this.text = text;
    }

    @Override
    public Object toTemplateVariable(final Request request) {
        return text;
    }
}
