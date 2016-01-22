package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoException;
import com.google.common.base.Optional;
import com.google.common.io.Files;

import java.io.File;
import java.nio.charset.Charset;

public class FileLogWriter implements LogWriter {
    private final File file;
    private Optional<Charset> charset;

    public FileLogWriter(final String filename, final Optional<Charset> charset) {
        this.file = new File(filename);
        this.charset = charset;
    }

    @Override
    public void write(final String content) {
        try {
            Files.append(content, file, charset.or(Charset.defaultCharset()));
        } catch (Exception e) {
            throw new MocoException(e);
        }
    }
}
