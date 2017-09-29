package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runner;
import com.github.dreamhead.moco.Server;
import com.github.dreamhead.moco.SocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StandaloneRunner {
    private static Logger logger = LoggerFactory.getLogger(StandaloneRunner.class);

    private Runner runner;

    public void run(final Server server) {
        runner = newRunner(server);
        runner.start();
        logger.info("Server is started at {}", server.port());
    }

    private Runner newRunner(final Server server) {
        if (server instanceof HttpServer) {
            return Runner.runner((HttpServer) server);
        }

        if (server instanceof SocketServer) {
            return Runner.runner((SocketServer) server);
        }

        throw new IllegalArgumentException("Unknown server type:" + server.getClass().getName());
    }

    public void stop() {
        if (runner != null) {
            runner.stop();
            logger.info("Server stopped.");
        }
    }
}
