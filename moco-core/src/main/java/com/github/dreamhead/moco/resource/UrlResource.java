package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

import java.io.IOException;
import java.net.URL;

import static com.google.common.io.ByteStreams.toByteArray;

public class UrlResource implements ContentResource {
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

    @Override
    public Resource apply(final MocoConfig config) {
        return this;
    }

    @Override
    public String getContentType() {
        return "text/html";
    }
}
