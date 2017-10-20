package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.parser.HttpArgsParser;

public final class HttpTask extends StartTask {
    public HttpTask(final String shutdownKey) {
        super(shutdownKey, new HttpArgsParser());
    }
}
