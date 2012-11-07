package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;

public class StandaloneRunner {
    public void run(HttpServer httpServer) {
        final MocoHttpServer server = new MocoHttpServer(httpServer);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stop();
            }
        });

        server.start();
    }
}
