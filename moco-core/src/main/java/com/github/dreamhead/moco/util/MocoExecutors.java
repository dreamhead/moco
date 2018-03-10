package com.github.dreamhead.moco.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MocoExecutors {
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static ExecutorService executor() {
        return executor;
    }

    private MocoExecutors() {
    }
}
