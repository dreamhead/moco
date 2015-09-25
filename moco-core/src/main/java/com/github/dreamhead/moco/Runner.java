package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.ActualSocketServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import com.github.dreamhead.moco.internal.MocoSocketServer;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Runner {
    public static void running(final HttpServer httpServer, final Runnable runnable) throws Exception {
        doRunning(runner(checkNotNull(httpServer)), checkNotNull(runnable));
    }

    public static void running(final HttpsServer httpServer, final Runnable runnable) throws Exception {
        doRunning(runner(checkNotNull(httpServer)), checkNotNull(runnable));
    }

    public static void running(final SocketServer server, final Runnable runnable) throws Exception {
        doRunning(runner(checkNotNull(server)), checkNotNull(runnable));
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
        return new MocoHttpServer((ActualHttpServer) checkNotNull(server, "Server should not be null"));
    }

    public static Runner runner(final SocketServer server) {
        return new MocoSocketServer((ActualSocketServer) checkNotNull(server, "Server should not be null"));
    }

    public abstract void start();
    public abstract void stop();
}
