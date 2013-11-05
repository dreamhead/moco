package com.github.dreamhead.moco.monitor;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileMonitor extends OutputMonitor {
    private final File file;

    public FileMonitor(String filename) {
        this.file = new File(filename);
    }

    protected void log(String content) {
        try {
            Files.append(content, file, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
