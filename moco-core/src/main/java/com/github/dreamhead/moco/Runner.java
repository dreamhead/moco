package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.MocoHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    public static void running(HttpServer httpServer, Runnable runnable) {
        MocoHttpServer server = new MocoHttpServer(httpServer);
        try {
            server.start();
            runnable.run();
        } catch (Throwable t) {
            logger.info("Exception caught", t);
            throw new RuntimeException(t);
        } finally {
            server.stop();
        }
    }
}
