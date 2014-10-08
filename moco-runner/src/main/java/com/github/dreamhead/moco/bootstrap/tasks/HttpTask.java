package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.ServerType;

public class HttpTask extends StartTask {
    public HttpTask(String shutdownKey) {
        super(shutdownKey, ServerType.HTTP);
    }
}
