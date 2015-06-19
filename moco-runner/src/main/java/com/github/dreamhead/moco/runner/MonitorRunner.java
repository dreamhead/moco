package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.runner.watcher.MocoRunnerWatcher;

public class MonitorRunner implements Runner {
    private final Runner runner;
    private final MocoRunnerWatcher mocoRunnerWatcher;

    public MonitorRunner(final Runner runner, final MocoRunnerWatcher mocoRunnerWatcher) {
        this.runner = runner;
        this.mocoRunnerWatcher = mocoRunnerWatcher;
    }

    @Override
    public void run() {
        this.runner.run();
        this.mocoRunnerWatcher.startMonitor();
    }

    @Override
    public void stop() {
        this.mocoRunnerWatcher.stopMonitor();
        this.runner.stop();
    }
}
