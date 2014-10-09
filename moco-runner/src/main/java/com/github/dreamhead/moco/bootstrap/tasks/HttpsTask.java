package com.github.dreamhead.moco.bootstrap.tasks;

import com.github.dreamhead.moco.bootstrap.ServerType;
import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.runner.Runner;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;

public class HttpsTask extends StartTask {
    public HttpsTask(String shutdownKey) {
        super(shutdownKey);
    }

    @Override
    protected Runner createRunner(String[] args) {
        StartArgs startArgs = parse(ServerType.HTTPS, args);
        return factory.createRunner(startArgs);
    }
}
