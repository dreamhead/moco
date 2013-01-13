package com.github.dreamhead.moco.resource;

public class TextResource implements Resource {
    private final String text;

    public TextResource(String text) {
        this.text = text;
    }

    @Override
    public String id() {
        return "text";
    }

    @Override
    public byte[] asByteArray() {
        return text.getBytes();
    }
}
