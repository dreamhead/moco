package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.*;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;
import static com.google.common.base.Optional.of;

public class StartTask implements BootstrapTask {
    private final int defaultShutdownPort;
    private final RunnerFactory factory;

    public StartTask(int defaultShutdownPort, String defaultShutdownKey) {
        this.factory = new RunnerFactory(defaultShutdownPort, defaultShutdownKey);
        this.defaultShutdownPort = defaultShutdownPort;
    }

    @Override
    public void run(String[] args) {
        StartArgs startArgs = parse(args);
        if (conflictWithDefaultShutdownPort(startArgs, defaultShutdownPort)) {
            System.err.println("port is same as default shutdown port, please specify another port or shutdown port.");
            return;
        }

        final Runner runner = factory.createRunner(startArgs);
        runner.run();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                runner.stop();
            }
        });
    }

    private boolean conflictWithDefaultShutdownPort(StartArgs startArgs, int defaultShutdownPort) {
        return startArgs.getPort().equals(of(defaultShutdownPort)) && !startArgs.hasShutdownPort();
    }
}
