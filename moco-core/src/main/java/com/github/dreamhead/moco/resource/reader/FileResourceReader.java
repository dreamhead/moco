package com.github.dreamhead.moco.resource.reader;

import com.github.dreamhead.moco.Request;
import com.google.common.base.Optional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.google.common.io.Files.toByteArray;
import static java.lang.String.format;

public class FileResourceReader extends AbstractFileResourceReader {
    private final File file;

    public FileResourceReader(File file, Optional<Charset> charset) {
        super(file.getName(), charset);
        this.file = file;
    }

    @Override
    protected byte[] doReadFor(final Optional<? extends Request> request) {
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
