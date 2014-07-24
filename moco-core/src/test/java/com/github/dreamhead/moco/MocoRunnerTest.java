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

    @Before
    public void setup() {
        HttpServer server = httpserver(port());
        server.response("foo");
        server.request(by(uri("/test"))).response("bar");
        runner = Runner.runner(server);
        runner.start();
        helper = new MocoTestHelper();
    }

    @After
    public void tearDown() {
        runner.stop();
    }

    @Test
    public void should_work_well() throws IOException {
        assertThat(helper.get(root()), is("foo"));
    }

    @Test
    public void should_work_well_for_sub_url() throws IOException {
        assertThat(helper.get(remoteUrl(port(), "/test")), is("bar"));
    }
}
