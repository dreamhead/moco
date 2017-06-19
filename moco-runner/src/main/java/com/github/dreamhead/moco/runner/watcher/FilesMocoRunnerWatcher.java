package com.github.dreamhead.moco.runner.watcher;

public class FilesMocoRunnerWatcher implements MocoRunnerWatcher {
    private final Iterable<ThreadSafeRunnerWatcher> monitors;

    public FilesMocoRunnerWatcher(final Iterable<ThreadSafeRunnerWatcher> monitors) {
        this.monitors = monitors;
    }

    @Override
    public void start() {
        for (ThreadSafeRunnerWatcher monitor : monitors) {
            monitor.start();
        }
    }

    @Override
    public void stop() {
        for (ThreadSafeRunnerWatcher monitor : monitors) {
            monitor.stop();
        }
    }
}
