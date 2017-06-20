package com.github.dreamhead.moco.runner.watcher;

public class CompositeRunnerWatcher implements MocoRunnerWatcher {
    private final Iterable<MocoRunnerWatcher> watchers;

    public CompositeRunnerWatcher(final Iterable<MocoRunnerWatcher> watchers) {
        this.watchers = watchers;
    }

    @Override
    public void start() {
        for (MocoRunnerWatcher watcher : watchers) {
            watcher.start();
        }
    }

    @Override
    public void stop() {
        for (MocoRunnerWatcher monitor : watchers) {
            monitor.stop();
        }
    }
}
