package com.github.dreamhead.moco.model;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.ByteStreams.toByteArray;

public class ContentStream {
    private byte[] bytes;

    public ContentStream(String text) {
        this.bytes = text.getBytes();
    }

    public ContentStream(InputStream is) {
        try {
            this.bytes = toByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] asByteArray() {
        return bytes;
    }
}
