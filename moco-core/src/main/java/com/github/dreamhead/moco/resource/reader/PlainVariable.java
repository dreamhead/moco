package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;

public class PlainVariable implements Variable {
    private Object text;

    public PlainVariable(Object text) {
        this.text = text;
    }

    @Override
    public Object toTemplateVariable(Request request) {
        return text;
    }
}
