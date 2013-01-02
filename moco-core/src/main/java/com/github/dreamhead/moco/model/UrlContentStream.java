package com.github.dreamhead.moco.model;

import java.io.IOException;
import java.net.URL;

import static com.google.common.io.ByteStreams.toByteArray;

public class UrlContentStream implements ContentStream {
    private URL url;

    public UrlContentStream(URL url) {
        this.url = url;
    }

    @Override
    public byte[] asByteArray() {
        try {
            return toByteArray(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
