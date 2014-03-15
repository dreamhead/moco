package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.RemoteTestUtils.httpsRoot;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.Runner.httpsRunner;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpsRunnerTest {
    private Runner runner;
    private MocoTestHelper helper;

    @Before
    public void setup() {
        HttpServer server = httpserver(port());
        server.response("foo");
        runner = httpsRunner(server);
        runner.start();
        helper = new MocoTestHelper();
    }

    @After
    public void tearDown() {
        runner.stop();
    }

    @Test
    public void should_work_well() throws IOException {
        assertThat(helper.get(httpsRoot()), is("foo"));
    }
}
