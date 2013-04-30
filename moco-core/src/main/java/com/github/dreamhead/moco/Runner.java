package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;

public class Runner {
    public static void running(HttpServer httpServer, Runnable runnable) throws Exception {
        MocoHttpServer server = new MocoHttpServer((ActualHttpServer)httpServer);
        try {
            server.start();
            runnable.run();
        } finally {
            server.stop();
        }
    }
}
