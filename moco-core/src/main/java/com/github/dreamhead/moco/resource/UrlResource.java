package com.github.dreamhead.moco.resource;

import java.io.IOException;
import java.net.URL;

import static com.google.common.io.ByteStreams.toByteArray;

public class UrlResource implements Resource {
    private final URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    @Override
    public String id() {
        return "url";
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
