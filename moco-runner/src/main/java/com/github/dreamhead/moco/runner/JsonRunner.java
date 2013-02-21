package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.parser.HttpServerParser;

import java.io.IOException;
import java.io.InputStream;

public class JsonRunner {

    static final HttpServerParser httpServerParser = new HttpServerParser();
    static final StandaloneRunner runner = new StandaloneRunner();

    public void run(InputStream is, int port) throws IOException {
        runner.run(httpServerParser.parseServer(is, port));
    }

    public void stop() {
        runner.stop();
    }

    public void restart(InputStream is, int port) throws IOException {
        HttpServer httpServer = httpServerParser.parseServer(is, port);
        stop();
        run(httpServer);
    }

    private void run(HttpServer httpServer) {
        runner.run(httpServer);
    }
}
