package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.ServerType;

public class HttpsTask extends StartTask {
    public HttpsTask(String shutdownKey, ServerType type) {
        super(shutdownKey, type);
    }
}
