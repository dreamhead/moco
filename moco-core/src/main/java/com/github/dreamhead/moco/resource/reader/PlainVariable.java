package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;

public class PlainVariable implements Variable {
    private String text;

    public PlainVariable(String text) {
        this.text = text;
    }

    @Override
    public String toString(Request request) {
        return text;
    }
}
