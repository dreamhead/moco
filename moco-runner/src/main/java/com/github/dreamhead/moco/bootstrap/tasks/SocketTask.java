package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.parser.SocketArgsParser;

public class SocketTask extends StartTask {
    public SocketTask(final String shutdownKey) {
        super(shutdownKey, new SocketArgsParser());
    }
}
