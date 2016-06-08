package com.github.dreamhead.moco;

import org.junit.Test;

import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.MocoJsonRunner.jsonHttpsServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.httpsRoot;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJsonHttpsRunnerTest extends AbstractMocoStandaloneTest {
    private final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");

    @Test
    public void should_return_expected_response() throws Exception {
        final HttpServer server = jsonHttpsServer(port(), file("src/test/resources/foo.json"), DEFAULT_CERTIFICATE);
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(httpsRoot()), is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_from_path_resource() throws Exception {
        final HttpServer server = jsonHttpsServer(port(), pathResource("foo.json"), DEFAULT_CERTIFICATE);
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(httpsRoot()), is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_without_port() throws Exception {
        final HttpServer server = jsonHttpsServer(file("src/test/resources/foo.json"), DEFAULT_CERTIFICATE);
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(httpsRoot(server.port())), is("foo"));
            }
        });
    }
}
