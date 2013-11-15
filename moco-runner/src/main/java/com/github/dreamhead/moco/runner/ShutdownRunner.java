package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.runner.watcher.ShutdownMocoRunnerWatcher;

public class ShutdownRunner extends MonitorRunner {
    private final ShutdownMocoRunnerWatcher monitor;

    public ShutdownRunner(Runner runner, ShutdownMocoRunnerWatcher mocoRunnerMonitor) {
        super(runner, mocoRunnerMonitor);
        this.monitor = mocoRunnerMonitor;
    }

    public int shutdownPort() {
        return this.monitor.port();
    }
}
