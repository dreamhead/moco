package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.parser.HttpServerParser;

import java.io.InputStream;
import java.util.List;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.collect.ImmutableList.of;

public class JsonRunner {

    private final HttpServerParser httpServerParser = new HttpServerParser();
    private final StandaloneRunner runner = new StandaloneRunner();

    public void run(InputStream streams, int port) {
        this.run(of(streams), port);
    }

    public void run(List<InputStream> streams, int port) {
        runner.run(createHttpServer(streams, port));
    }

    private HttpServer createHttpServer(List<InputStream> streams, int port) {
        HttpServer server = new ActualHttpServer(port);

        for (InputStream stream : streams) {
            HttpServer parsedServer = httpServerParser.parseServer(stream, port);
            server = mergeServer(server, parsedServer);
        }

        return server;
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

    private HttpServer mergeServer(HttpServer server, HttpServer parsedServer) {
        ActualHttpServer thisServer = (ActualHttpServer) server;
        return thisServer.mergeHttpServer(parsedServer);
    }
}
