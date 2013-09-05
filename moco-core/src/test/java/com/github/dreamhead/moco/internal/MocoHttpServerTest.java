package com.github.dreamhead.moco.internal;

import org.junit.Test;

public class MocoHttpServerTest {
    @Test
    public void should_stop_stoped_server_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(8080));
        server.stop();
    }

    @Test
    public void should_stop_server_many_times_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(8080));
        server.start();
        server.stop();
        server.stop();
    }
}
