package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.util.FileContentType;

import java.io.IOException;

import static com.google.common.io.ByteStreams.toByteArray;

public class ClasspathFileResource implements ContentResource {
    private String filename;

    public ClasspathFileResource(String filename) {
        this.filename = filename;
    }

    @Override
    public String id() {
        return "pathresource";
    }

    @Override
    public byte[] asByteArray() {
        try {
            return toByteArray(this.getClass().getClassLoader().getResourceAsStream(filename));
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
        return new FileContentType(this.filename).getContentType();
    }
}
