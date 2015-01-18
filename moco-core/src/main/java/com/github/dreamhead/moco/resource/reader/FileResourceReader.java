package com.github.dreamhead.moco.resource.reader;

import com.google.common.base.Optional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.google.common.io.Files.toByteArray;
import static java.lang.String.format;

public class FileResourceReader extends AbstractFileResourceReader {
    private final File file;

    public FileResourceReader(File file, Optional<Charset> charset) {
        super(charset, file.getName());
        this.file = file;
    }

    @Override
    protected byte[] doReadFor() {
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
