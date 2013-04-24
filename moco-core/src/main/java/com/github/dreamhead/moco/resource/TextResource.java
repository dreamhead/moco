package com.github.dreamhead.moco.resource;

public class TextResource implements ContentResource {
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

    @Override
    public String getContentType() {
        return "text/plain; charset=UTF-8";
    }
}
