package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoException;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public final class FileLogWriter implements LogWriter {
    private final File file;
    private final Charset charset;

    public FileLogWriter(final String filename, final Charset charset) {
        this.file = new File(filename);
        this.charset = asCharset(charset);
    }

    private Charset asCharset(final Charset charset) {
        if (charset != null) {
            return charset;
        }

        return Charset.defaultCharset();
    }

    @Override
    public void write(final String content) {
        try {
            Files.writeString(file.toPath(), content, charset,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (Exception e) {
            throw new MocoException(e);
        }
    }
}
