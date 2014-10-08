package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.ServerType;

public class SocketTask extends StartTask {
    public SocketTask(String shutdownKey) {
        super(shutdownKey, ServerType.SOCKET);
    }
}
