package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.server.ServerRunner;
import org.junit.jupiter.api.Test;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;

public class MocoHttpServerTest {
    @Test
    public void should_stop_stopped_server_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(port()));
        new ServerRunner(server).stop();
    }

    @Test
    public void should_stop_server_many_times_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(port()));
        ServerRunner serverRunner = new ServerRunner(server);
        serverRunner.start();
        serverRunner.stop();
        serverRunner.stop();
    }
}
