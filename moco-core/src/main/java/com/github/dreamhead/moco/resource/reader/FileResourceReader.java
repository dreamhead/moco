package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.util.FileContentType;
import com.google.common.base.Optional;

import java.io.File;
import java.io.IOException;

import static com.google.common.io.Files.toByteArray;
import static java.lang.String.format;

public class FileResourceReader implements ContentResourceReader {
    private final File file;

    public FileResourceReader(File file) {
        this.file = file;
    }

    @Override
    public String getContentType() {
        return new FileContentType(file.getName()).getContentType();
    }

    @Override
    public byte[] readFor(final Optional<? extends Request> request) {
        if (!file.exists()) {
            throw new IllegalArgumentException(format("%s does not exist", file.getPath()));
        }

        try {
            return toByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
