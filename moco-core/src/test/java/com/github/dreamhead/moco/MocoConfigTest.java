package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.MocoMount.to;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoConfigTest {
    private static final String MOUNT_DIR = "src/test/resources/test";

    private HttpServer server;
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
    }

    @Test
    public void should_config_context() throws Exception {
        server = httpserver(port(), context("/context"));

        server.get(by(uri("/foo"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/context/foo")), is("foo"));
            }
        });
    }

    @Test
    public void should_mount_correctly() throws Exception {
        server = httpserver(port(), context("/context"));

        server.mount(MOUNT_DIR, to("/dir"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/context/dir/dir.response")), is("response from dir"));
            }
        });
    }
}
