package com.github.moco.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Stream {
    private InputStream is;

    public Stream(String text) {
        this.is = new ByteArrayInputStream(text.getBytes());
    }

    public Stream(InputStream is) {
        this.is = is;
    }

    public InputStream asInputStream() {
        return is;
    }
}
