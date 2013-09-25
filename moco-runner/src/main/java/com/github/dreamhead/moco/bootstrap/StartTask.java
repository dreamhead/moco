package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.Runner;
import com.github.dreamhead.moco.runner.RunnerFactory;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;

public class StartTask implements BootstrapTask {
    private final RunnerFactory factory;

    public StartTask(String defaultShutdownKey) {
        this.factory = new RunnerFactory(defaultShutdownKey);
    }

    @Override
    public void run(String[] args) {
        StartArgs startArgs = parse(args);

        final Runner runner = factory.createRunner(startArgs);
        runner.run();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                runner.stop();
            }
        });
    }
}
