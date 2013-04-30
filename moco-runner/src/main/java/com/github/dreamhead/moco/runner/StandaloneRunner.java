package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneRunner {
    private static Logger logger = LoggerFactory.getLogger(StandaloneRunner.class);

    private MocoHttpServer server;

    public void run(HttpServer httpServer) {
        ActualHttpServer actualHttpServer = (ActualHttpServer) httpServer;
        server = new MocoHttpServer(actualHttpServer);
        logger.info("Server is started at {}", actualHttpServer.getPort());
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop();
            logger.info("Server stopped.");
        }
    }
}
