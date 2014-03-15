package com.github.dreamhead.moco.internal;

import org.junit.Test;

import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.google.common.base.Optional.of;

public class MocoHttpsServerTest {
    @Test
    public void should_stop_stoped_server_without_exception() {
        MocoHttpsServer server = new MocoHttpsServer(ActualHttpServer.createLogServer(of(port())));
        server.stop();
    }

    @Test
    public void should_stop_server_many_times_without_exception() {
        MocoHttpsServer server = new MocoHttpsServer(ActualHttpServer.createLogServer(of(port())));
        server.start();
        server.stop();
        server.stop();
    }
}
