package com.github.dreamhead.moco.junit;

import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.HttpsServer;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static com.github.dreamhead.moco.Moco.httpsServer;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.httpsRoot;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJunitPojoHttpsRunnerTest extends AbstractMocoStandaloneTest {
    private static final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");

    private static HttpsServer server;

    static {
        server = httpsServer(12306, DEFAULT_CERTIFICATE);
        server.response("foo");
    }

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.httpsRunner(server);

    @Test
    public void should_return_expected_message() throws IOException {
        assertThat(helper.get(httpsRoot()), is("foo"));
    }
}
