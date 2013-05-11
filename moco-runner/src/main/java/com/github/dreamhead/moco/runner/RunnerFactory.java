package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.StartArgs;

public class RunnerFactory {
    private final int defaultShutdownPort;
    private final String defaultShutdownKey;

    public RunnerFactory(int defaultShutdownPort, String defaultShutdownKey) {
        this.defaultShutdownPort = defaultShutdownPort;
        this.defaultShutdownKey = defaultShutdownKey;
    }

    public Runner createShutdownMonitor(final Runner runner, final int shutdownPort, final String shutdownKey) {
        return new MonitorRunner(runner, new ShutdownMonitor(shutdownPort, shutdownKey, new ShutdownListener() {
            @Override
            public void onShutdown() {
                runner.stop();
            }
        }));
    }

    public Runner createRunner(StartArgs startArgs) {
        final Runner dynamicRunner = new DynamicRunner(startArgs.getConfigurationFile(), startArgs.getPort());
        return createShutdownMonitor(dynamicRunner, startArgs.getShutdownPort(defaultShutdownPort), defaultShutdownKey);
    }
}
