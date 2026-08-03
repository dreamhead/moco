package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import com.github.dreamhead.moco.server.ServerRunner;

import static java.util.Objects.requireNonNull;

public abstract class Runner {
    public static void running(final HttpServer httpServer, final Runnable runnable) throws Exception {
        doRunning(runner(requireNonNull(httpServer)), requireNonNull(runnable));
    }

    public static void running(final HttpsServer httpServer, final Runnable runnable) throws Exception {
        doRunning(runner(requireNonNull(httpServer)), requireNonNull(runnable));
    }

    private static void doRunning(final Runner server, final Runnable runnable) throws Exception {
        try {
            server.start();
            runnable.run();
        } finally {
            server.stop();
        }
    }

    public static Runner runner(final HttpServer server) {
        return new ServerRunner(new MocoHttpServer((ActualHttpServer) requireNonNull(server, "Server should not be null")));
    }

    public abstract void start();
    public abstract void stop();
}
