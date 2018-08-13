package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.runner.watcher.ShutdownMocoRunnerWatcher;

public final class ShutdownRunner extends WatcherRunner {
    private final ShutdownMocoRunnerWatcher monitor;

    public ShutdownRunner(final Runner runner,
                          final ShutdownMocoRunnerWatcher watcher) {
        super(runner, watcher);
        this.monitor = watcher;
    }

    public int shutdownPort() {
        return this.monitor.port();
    }
}
