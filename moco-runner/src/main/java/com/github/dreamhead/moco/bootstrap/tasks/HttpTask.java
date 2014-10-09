package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.ServerType;
import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.runner.Runner;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;

public class HttpTask extends StartTask {
    public HttpTask(String shutdownKey) {
        super(shutdownKey, ServerType.HTTP);
    }

    @Override
    protected Runner createRunner(String[] args) {
        StartArgs startArgs = parse(type, args);
        return factory.createRunner(startArgs);
    }
}
