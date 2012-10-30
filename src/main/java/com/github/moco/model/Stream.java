package com.github.moco.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Stream {
    private String text;

    public Stream(String text) {
        this.text = text;
    }

    public InputStream asInputStream() {
        return new ByteArrayInputStream(text.getBytes());
    }
}
