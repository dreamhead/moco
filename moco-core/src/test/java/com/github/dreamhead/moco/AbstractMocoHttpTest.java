package com.github.dreamhead.moco;

import static com.github.dreamhead.moco.Moco.httpServer;

public class AbstractMocoHttpTest extends BaseMocoHttpTest<HttpServer> {
    protected HttpServer createServer(int port) {
        return httpServer(port);
    }
}
