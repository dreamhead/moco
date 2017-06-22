package com.github.dreamhead.moco.runner.watcher;

import java.util.concurrent.TimeUnit;

public class ThreadSafeRunnerWatcher implements Watcher {
    public static final long INTERVAL = TimeUnit.SECONDS.toMillis(1);

    private final Watcher watcher;
    private boolean running = false;

    public ThreadSafeRunnerWatcher(final Watcher watcher) {
        this.watcher = watcher;
    }

    public synchronized void start() {
        watcher.start();
        running = true;
    }

    public synchronized void stop() {
        if (watcher != null && running) {
            watcher.stop();
            running = false;
        }
    }
}
