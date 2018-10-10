package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoPortTest {

    private MocoTestHelper helper;

    @Before
    public void setUp() {
        helper = new MocoTestHelper();
    }

    @Test
    public void should_create_http_server_without_specific_port() throws Exception {
        final HttpServer server = httpServer();
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(root(server.port())), is("foo"));
            }
        });
    }

    @Test(expected = IllegalStateException.class)
    public void should_not_get_port_without_binding() throws Exception {
        final HttpServer server = httpServer();
        server.port();
    }
}
