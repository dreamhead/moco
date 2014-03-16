package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Test;

import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.internal.ActualHttpServer.createLogServer;
import static com.google.common.base.Optional.of;

public class MocoHttpsServerTest {

    @Test
    public void should_stop_stoped_server_without_exception() {
        MocoHttpsServer server = new MocoHttpsServer(createLogServer(of(port())), MocoTestHelper.CERTIFICATE);
        server.stop();
    }

    @Test
    public void should_stop_server_many_times_without_exception() {
        MocoHttpsServer server = new MocoHttpsServer(createLogServer(of(port())), MocoTestHelper.CERTIFICATE);
        server.start();
        server.stop();
        server.stop();
    }
}
