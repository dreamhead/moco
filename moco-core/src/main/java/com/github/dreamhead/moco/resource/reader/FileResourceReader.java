package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.util.FileContentType;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.File;
import java.io.IOException;

import static com.google.common.io.Files.toByteArray;

public class FileResourceReader implements ContentResourceReader {
    private File file;

    public FileResourceReader(File file) {
        this.file = file;
    }

    @Override
    public String getContentType() {
        return new FileContentType(file.getName()).getContentType();
    }

    @Override
    public byte[] readFor(FullHttpRequest request) {
        try {
            return toByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
