package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRunnerTest {
    private Runner runner;
    private MocoTestHelper helper;

    @Before
    public void setup() {
        HttpServer server = httpServer(port());
        server.response("foo");
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
}
