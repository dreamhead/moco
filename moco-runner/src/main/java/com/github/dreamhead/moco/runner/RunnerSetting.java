package com.github.dreamhead.moco.runner;

import com.google.common.base.Optional;

import java.io.InputStream;

public class RunnerSetting {
    private InputStream stream;
    private final Optional<String> context;
    private final Optional<String> fileRoot;

    public RunnerSetting(InputStream stream, String context, String fileRoot) {
        this.stream = stream;
        this.context = Optional.fromNullable(context);
        this.fileRoot = Optional.fromNullable(fileRoot);
    }

    public InputStream getStream() {
        return stream;
    }

    public Optional<String> getContext() {
        return context;
    }

    public Optional<String> getFileRoot() {
        return fileRoot;
    }
}
