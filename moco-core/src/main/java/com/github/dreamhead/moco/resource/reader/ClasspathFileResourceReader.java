package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;

import java.io.IOException;

import static com.google.common.io.ByteStreams.toByteArray;

public class ClasspathFileResourceReader implements ContentResourceReader {
    private final String filename;

    public ClasspathFileResourceReader(String filename) {
        this.filename = filename;
    }

    @Override
    public byte[] readFor(final Optional<? extends Request> request) {
        try {
            return toByteArray(this.getClass().getClassLoader().getResourceAsStream(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContentType() {
        return new FileContentType(this.filename).getContentType();
    }
}
