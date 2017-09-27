package com.github.dreamhead.moco.runner.watcher;

public final class Watchers {
    public static Watcher threadSafe(final Watcher watcher) {
        return new ThreadSafeWatcher(watcher);
    }

    private static class ThreadSafeWatcher implements Watcher {
        private final Watcher watcher;
        private boolean running = false;

        private ThreadSafeWatcher(final Watcher watcher) {
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
