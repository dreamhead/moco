package com.github.dreamhead.moco.runner.watcher;

public class CompositeRunnerWatcher implements RunnerWatcher {
    private final Iterable<RunnerWatcher> watchers;

    public CompositeRunnerWatcher(final Iterable<RunnerWatcher> watchers) {
        this.watchers = watchers;
    }

    @Override
    public void start() {
        for (RunnerWatcher watcher : watchers) {
            watcher.start();
        }
    }

    @Override
    public void stop() {
        for (RunnerWatcher monitor : watchers) {
            monitor.stop();
        }
    }
}
