package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.parser.HttpArgsParser;

public class HttpsTask extends StartTask {
    public HttpsTask(final String shutdownKey) {
        super(shutdownKey, new HttpArgsParser());
    }
}
