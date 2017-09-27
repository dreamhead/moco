package com.github.dreamhead.moco.runner.watcher;

public final class CompositeWatcher implements Watcher {
    private final Iterable<Watcher> watchers;

    public CompositeWatcher(final Iterable<Watcher> watchers) {
        this.watchers = watchers;
    }

    @Override
    public void start() {
        for (Watcher watcher : watchers) {
            watcher.start();
        }
    }

    @Override
    public void stop() {
        for (Watcher monitor : watchers) {
            monitor.stop();
        }
    }
}
