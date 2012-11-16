package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;

public class StandaloneRunner {
    private MocoHttpServer server;

    public void run(HttpServer httpServer) {
        server = new MocoHttpServer(httpServer);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stop();
            }
        });

        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
