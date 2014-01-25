package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;

public abstract class Runner {
    public static void running(HttpServer httpServer, Runnable runnable) throws Exception {
        Runner server = runner(httpServer);
        try {
            server.start();
            runnable.run();
        } finally {
            server.stop();
        }
    }

    public static Runner runner(HttpServer httpServer) {
        return new MocoHttpServer((ActualHttpServer)httpServer);
    }

    public abstract void start();
    public abstract void stop();
}
