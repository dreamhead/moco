package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneRunner {
    private static Logger logger = LoggerFactory.getLogger(StandaloneRunner.class);

    private MocoHttpServer server;

    public void run(HttpServer httpServer) {
        server = new MocoHttpServer(httpServer);

        logger.info("Server is started at {}", httpServer.getPort());

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
            logger.info("Server stopped.");
        }
    }
}
