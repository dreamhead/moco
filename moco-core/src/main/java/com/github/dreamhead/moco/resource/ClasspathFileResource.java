package com.github.dreamhead.moco.resource;

import java.io.IOException;

import static com.google.common.io.ByteStreams.toByteArray;

public class ClasspathFileResource implements Resource {
    private String filename;

    public ClasspathFileResource(String filename) {
        this.filename = filename;
    }

    @Override
    public String id() {
        return "classpathfile";
    }

    @Override
    public byte[] asByteArray() {
        try {
            return toByteArray(this.getClass().getClassLoader().getResourceAsStream(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
