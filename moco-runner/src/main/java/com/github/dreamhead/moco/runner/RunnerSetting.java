package com.github.dreamhead.moco.runner;

import com.google.common.base.Optional;

import java.io.InputStream;

public class RunnerSetting {
    private InputStream stream;
    private Optional<String> context;

    public RunnerSetting(InputStream stream, String context) {
        this.stream = stream;
        this.context = Optional.fromNullable(context);
    }

    public InputStream getStream() {
        return stream;
    }

    public Optional<String> getContext() {
        return context;
    }
}
