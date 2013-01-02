package com.github.dreamhead.moco.model;

import java.io.File;
import java.io.IOException;

import static com.google.common.io.Files.toByteArray;

public class FileContentStream implements ContentStream {
    private File file;

    public FileContentStream(File file) {
        if (!file.exists()) {
            throw new RuntimeException(String.format("File %s not found", file.getName()));
        }
        this.file = file;
    }

    @Override
    public byte[] asByteArray() {
        try {
            return toByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
