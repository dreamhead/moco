package com.github.moco.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ContentStream {
    private InputStream is;

    public ContentStream(String text) {
        this.is = new ByteArrayInputStream(text.getBytes());
    }

    public ContentStream(InputStream is) {
        this.is = is;
    }

    public InputStream asInputStream() {
        return is;
    }
}
