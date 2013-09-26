package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.runner.monitor.ShutdownMocoRunnerMonitor;

public class ShutdownRunner extends MonitorRunner {
    private final ShutdownMocoRunnerMonitor monitor;

    public ShutdownRunner(Runner runner, ShutdownMocoRunnerMonitor mocoRunnerMonitor) {
        super(runner, mocoRunnerMonitor);
        this.monitor = mocoRunnerMonitor;
    }

    public int shutdownPort() {
        return this.monitor.port();
    }
}
