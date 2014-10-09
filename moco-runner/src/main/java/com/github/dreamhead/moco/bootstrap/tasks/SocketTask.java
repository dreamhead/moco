package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.ServerType;
import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.runner.Runner;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;

public class SocketTask extends StartTask {
    public SocketTask(String shutdownKey) {
        super(shutdownKey);
    }

    @Override
    protected Runner createRunner(String[] args) {
        StartArgs startArgs = parse(ServerType.SOCKET, args);
        return factory.createRunner(startArgs);
    }
}
