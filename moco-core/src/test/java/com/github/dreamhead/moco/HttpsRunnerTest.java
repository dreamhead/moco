package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.httpsServer;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.httpsRoot;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpsRunnerTest {
    private Runner runner;
    private MocoTestHelper helper;

    @Before
    public void setup() {
        HttpsServer server = httpsServer(port(), certificate(pathResource("cert.jks"), "mocohttps", "mocohttps"));
        server.response("foo");
        this.runner = Runner.runner(server);
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
