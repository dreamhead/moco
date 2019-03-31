package com.github.dreamhead.moco.internal;

import org.junit.Test;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.google.common.base.Optional.of;

public class MocoHttpServerTest {
    @Test
    public void should_stop_stoped_server_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(port()));
        server.stop();
    }

    @Test
    public void should_stop_server_many_times_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(port()));
        server.start();
        server.stop();
        server.stop();
    }
}
