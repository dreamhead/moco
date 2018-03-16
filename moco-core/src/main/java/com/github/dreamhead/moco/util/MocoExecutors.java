package com.github.dreamhead.moco.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MocoExecutors {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static ExecutorService executor() {
        return EXECUTOR;
    }

    private MocoExecutors() {
    }
}
