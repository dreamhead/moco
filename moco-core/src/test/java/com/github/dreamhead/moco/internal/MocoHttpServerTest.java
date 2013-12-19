package com.github.dreamhead.moco.internal;

import static com.github.dreamhead.moco.RemoteTestUtils.*;
import static com.google.common.base.Optional.*;

import org.junit.Test;

public class MocoHttpServerTest {
    @Test
    public void should_stop_stoped_server_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(of(port())));
        server.stop();
    }

    @Test
    public void should_stop_server_many_times_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(of(port())));
        server.start();
        server.stop();
        server.stop();
    } 
	
	@Test	
    public void should_start_stoped_server_without_exception() {
        MocoHttpServer server = new MocoHttpServer(ActualHttpServer.createLogServer(of(port())));
        server.start();
        server.stop();
		server = new MocoHttpServer(ActualHttpServer.createLogServer(of(port())));
		server.start();
        server.stop();
    }
}
