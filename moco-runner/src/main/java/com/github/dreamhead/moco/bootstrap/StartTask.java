package com.github.dreamhead.moco.bootstrap;

import com.github.dreamhead.moco.runner.DynamicRunner;
import com.github.dreamhead.moco.runner.Runner;
import com.github.dreamhead.moco.runner.SocketShutdownMonitorRunner;

import static com.github.dreamhead.moco.bootstrap.StartArgs.parse;

public class StartTask implements BootstrapTask {
    private final int defaultShutdownPort;
    private final String defaultShutdownKey;

    public StartTask(int defaultShutdownPort, String defaultShutdownKey) {
        this.defaultShutdownPort = defaultShutdownPort;
        this.defaultShutdownKey = defaultShutdownKey;
    }

    @Override
    public void run(String[] args) {
        StartArgs startArgs = parse(args);
        if (conflictWithDefaultShutdownPort(startArgs, defaultShutdownPort)) {
            System.err.println("port is same as default shutdown port, please specify another port or shutdown port.");
            return;
        }

        final Runner runner = createRunner(startArgs);
        runner.run();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                runner.stop();
            }
        });
    }

    private Runner createRunner(StartArgs startArgs) {
        final Runner dynamicRunner = new DynamicRunner(startArgs.getConfigurationFile(), startArgs.getPort());
        return new SocketShutdownMonitorRunner(dynamicRunner, startArgs.getShutdownPort(defaultShutdownPort), defaultShutdownKey);
    }

    private boolean conflictWithDefaultShutdownPort(StartArgs startArgs, int defaultShutdownPort) {
        return startArgs.getPort() == defaultShutdownPort && !startArgs.hasShutdonwPort();
    }
}
