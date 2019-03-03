package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoException;
import com.google.common.io.Files;

import java.io.File;
import java.nio.charset.Charset;

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
            Files.append(content, file, charset);
        } catch (Exception e) {
            throw new MocoException(e);
        }
    }
}
