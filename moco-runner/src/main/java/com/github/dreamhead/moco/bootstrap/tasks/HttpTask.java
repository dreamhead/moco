package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.ServerType;
import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.runner.Runner;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;

public class HttpTask extends StartTask {
    public HttpTask(final String shutdownKey) {
        super(shutdownKey);
    }

    @Override
    protected Runner createRunner(final String[] args) {
        StartArgs startArgs = parse(ServerType.HTTP, args);
        return factory.createRunner(startArgs);
    }
}
