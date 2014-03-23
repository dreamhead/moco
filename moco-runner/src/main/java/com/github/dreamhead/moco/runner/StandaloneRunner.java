package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneRunner {
    private static Logger logger = LoggerFactory.getLogger(StandaloneRunner.class);

    private Runner runner;

    public void run(HttpServer httpServer) {
        runner = Runner.runner(httpServer);
        runner.start();
        logger.info("Server is started at {}", httpServer.port());
    }

    public void stop() {
        if (runner != null) {
            runner.stop();
            logger.info("Server stopped.");
        }
    }
}
