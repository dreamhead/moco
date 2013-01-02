package com.github.dreamhead.moco.model;

public class StringContentStream implements ContentStream {
    private final String text;

    public StringContentStream(String text) {
        this.text = text;
    }

    @Override
    public byte[] asByteArray() {
        return text.getBytes();
    }
}
