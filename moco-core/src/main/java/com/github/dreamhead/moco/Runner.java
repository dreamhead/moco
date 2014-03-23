package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.*;

public abstract class Runner {
    public static void running(HttpServer httpServer, Runnable runnable) throws Exception {
        doRunning(runner(httpServer), runnable);
    }

    public static void running(HttpsServer httpServer, Runnable runnable) throws Exception {
        doRunning(runner(httpServer), runnable);
    }

    private static void doRunning(Runner server, Runnable runnable) throws Exception {
        try {
            server.start();
            runnable.run();
        } finally {
            server.stop();
        }
    }

    public static Runner runner(HttpServer server) {
        return new MocoHttpServer((ActualHttpServer) server);
    }

    public static Runner runner(HttpsServer server) {
        return new MocoHttpsServer((ActualHttpsServer) server);
    }

    public abstract void start();
    public abstract void stop();
}
