package com.github.dreamhead.moco.runner.watcher;

import java.util.concurrent.TimeUnit;

public class Watchers {
    public static final long INTERVAL = TimeUnit.SECONDS.toMillis(1);

    public static Watcher threadSafe(final Watcher watcher) {
        return new ThreadSafeRunnerWatcher(watcher);
    }

    private static class ThreadSafeRunnerWatcher implements Watcher {
        private final Watcher watcher;
        private boolean running = false;

        ThreadSafeRunnerWatcher(final Watcher watcher) {
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

    private Watchers() {
    }
}
