package com.github.dreamhead.moco.monitor;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileLogWriter implements LogWriter {
    private final File file;

    public FileLogWriter(String filename) {
        this.file = new File(filename);
    }

    @Override
    public void write(String content) {
        try {
            Files.append(content, file, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
