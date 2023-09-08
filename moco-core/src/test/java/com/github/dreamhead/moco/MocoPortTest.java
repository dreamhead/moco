package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MocoPortTest {

    private MocoTestHelper helper;

    @BeforeEach
    public void setUp() {
        helper = new MocoTestHelper();
    }

    @Test
    public void should_create_http_server_without_specific_port() throws Exception {
        final HttpServer server = httpServer();
        server.response("foo");

        running(server, () -> assertThat(helper.get(root(server.port())), is("foo")));
    }

    @Test
    public void should_not_get_port_without_binding() {
        final HttpServer server = httpServer();
        assertThrows(IllegalStateException.class, () -> server.port());
    }
}
