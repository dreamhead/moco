package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoPortTest {
    protected HttpServer server;
    protected MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
    }

    @Test
    public void should_create_server_without_specific_port() throws Exception {
        final HttpServer server = httpserver();
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
        final HttpServer server = httpserver();
        server.port();
    }
}
