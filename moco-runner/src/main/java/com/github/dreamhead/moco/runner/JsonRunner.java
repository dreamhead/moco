package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.parser.HttpServerParser;

import java.io.IOException;
import java.io.InputStream;

public class JsonRunner {
    private final HttpServerParser httpServerParser = new HttpServerParser();
    private final StandaloneRunner runner = new StandaloneRunner();

    public void run(InputStream is) throws IOException {
        HttpServer server = httpServerParser.parseServer(is);
        runner.run(server);
    }

    public void stop() {
        runner.stop();
    }
}
