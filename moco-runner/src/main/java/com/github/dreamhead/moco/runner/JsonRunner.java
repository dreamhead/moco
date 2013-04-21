package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.parser.HttpServerParser;

import java.io.InputStream;

import static com.github.dreamhead.moco.Moco.*;

public class JsonRunner {

    private final HttpServerParser httpServerParser = new HttpServerParser();
    private final StandaloneRunner runner = new StandaloneRunner();

    public void run(InputStream is, int port) {
        run(createServer(is, port));
    }

    public void stop() {
        runner.stop();
    }

    public void restart(InputStream is, int port) {
        HttpServer httpServer = createServer(is, port);
        stop();
        run(httpServer);
    }

    private void run(HttpServer httpServer) {
        runner.run(httpServer);
    }

    private HttpServer createServer(InputStream is, int port) {
        HttpServer httpServer = httpServerParser.parseServer(is, port);
        httpServer.request(by(uri("/favicon.ico"))).response(content(pathResource("favicon.png")), header("Content-Type", "image/png"));
        return httpServer;
    }
}
