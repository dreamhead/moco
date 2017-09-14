package com.github.dreamhead.moco.util;

import java.util.concurrent.locks.Lock;

public final class Locks {
    public static void withLock(final Lock lock, final Runnable runnable) {
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    private Locks() {
    }
}
