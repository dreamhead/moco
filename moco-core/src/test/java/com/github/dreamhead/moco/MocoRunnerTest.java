package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRunnerTest {
    private Runner runner;
    private MocoTestHelper helper;
    private HttpServer server;

    @Before
    public void setup() {
        server = httpserver(port());
        runner = Runner.runner(server);
        helper = new MocoTestHelper();
    }

    @After
    public void tearDown() {
        runner.stop();
    }

    @Test
    public void should_work_well() throws IOException {
        server.response("foo");
        runner.start();
        assertThat(helper.get(root()), is("foo"));
    }

    @Test
    public void should_work_well_for_sub_url() throws IOException {
        server.request(by(uri("/test"))).response("bar");
        runner.start();
        assertThat(helper.get(remoteUrl(port(), "/test")), is("bar"));
    }
}
