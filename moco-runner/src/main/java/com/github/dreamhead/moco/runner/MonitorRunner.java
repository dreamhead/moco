package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.runner.watcher.RunnerWatcher;

public class MonitorRunner implements Runner {
    private final Runner runner;
    private final RunnerWatcher watcher;

    public MonitorRunner(final Runner runner, final RunnerWatcher watcher) {
        this.runner = runner;
        this.watcher = watcher;
    }

    @Override
    public void run() {
        this.runner.run();
        this.watcher.start();
    }

    @Override
    public void stop() {
        this.watcher.stop();
        this.runner.stop();
    }
}
