package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.util.FileContentType;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.IOException;

import static com.google.common.io.ByteStreams.toByteArray;

public class ClasspathFileResourceReader implements ContentResourceReader {
    private String filename;

    public ClasspathFileResourceReader(String filename) {
        this.filename = filename;
    }

    @Override
    public byte[] readFor(FullHttpRequest request) {
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
