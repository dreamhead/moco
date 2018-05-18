package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.runner.watcher.Watcher;

public class WatcherRunner implements Runner {
    private final Runner runner;
    private final Watcher watcher;

    public WatcherRunner(final Runner runner, final Watcher watcher) {
        this.runner = runner;
        this.watcher = watcher;
    }

    @Override
    public final void run() {
        this.runner.run();
        this.watcher.start();
    }

    @Override
    public final void stop() {
        this.watcher.stop();
        this.runner.stop();
    }
}
