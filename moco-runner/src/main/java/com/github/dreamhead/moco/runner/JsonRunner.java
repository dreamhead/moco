package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.parser.HttpServerParser;

import java.io.InputStream;

import static com.github.dreamhead.moco.Moco.*;

public class JsonRunner implements Runner {

    private final HttpServerParser httpServerParser = new HttpServerParser();
    private final StandaloneRunner runner = new StandaloneRunner();
    private HttpServer httpServer;

    public JsonRunner(Iterable<? extends InputStream> streams, int port) {
        this.httpServer = createHttpServer(streams, port);
    }

    public void run() {
        runner.run(httpServer);
    }

    public void stop() {
        runner.stop();
    }

    private HttpServer createHttpServer(Iterable<? extends InputStream> streams, int port) {
        HttpServer server = createBaseHttpServer(streams, port);
        server.request(by(uri("/favicon.ico"))).response(content(pathResource("favicon.png")), header("Content-Type", "image/png"));
        return server;
    }

    private HttpServer createBaseHttpServer(Iterable<? extends InputStream> streams, int port) {
        HttpServer server = new ActualHttpServer(port);

        for (InputStream stream : streams) {
            HttpServer parsedServer = httpServerParser.parseServer(stream, port);
            server = mergeServer(server, parsedServer);
        }

        return server;
    }

    private HttpServer mergeServer(HttpServer server, HttpServer parsedServer) {
        ActualHttpServer thisServer = (ActualHttpServer) server;
        return thisServer.mergeHttpServer((ActualHttpServer)parsedServer);
    }
}
