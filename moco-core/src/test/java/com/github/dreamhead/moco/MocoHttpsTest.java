package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.httpsServer;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoRequestHit.once;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.httpsRoot;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteHttpsUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoHttpsTest {
    private static final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
    }

    @Test
    public void should_return_expected_result() throws Exception {
        HttpsServer server = httpsServer(port(), DEFAULT_CERTIFICATE);
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(httpsRoot()), is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_result_for_specified_request() throws Exception {
        HttpsServer server = httpsServer(port(), DEFAULT_CERTIFICATE);
        server.request(by("foo")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(httpsRoot(), "foo"), is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_result_with_monitor() throws Exception {
        RequestHit hit = requestHit();
        HttpsServer server = httpsServer(port(), DEFAULT_CERTIFICATE, hit);
        server.request(by("foo")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(httpsRoot(), "foo"), is("bar"));
            }
        });

        hit.verify(by("foo"), once());
    }

    @Test
    public void should_return_expected_result_without_port() throws Exception {
        final HttpsServer server = httpsServer(DEFAULT_CERTIFICATE);
        server.request(by("foo")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(httpsRoot(server.port()), "foo"), is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_result_with_monitor_without_port() throws Exception {
        RequestHit hit = requestHit();
        final HttpsServer server = httpsServer(DEFAULT_CERTIFICATE, hit);
        server.request(by("foo")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(httpsRoot(server.port()), "foo"), is("bar"));
            }
        });

        hit.verify(by("foo"), once());
    }

    @Test
    public void should_return_expected_result_with_global_config() throws Exception {
        HttpsServer server = httpsServer(port(), DEFAULT_CERTIFICATE, context("/foo"));
        server.request(by(uri("/bar"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteHttpsUrl("/foo/bar")), is("foo"));
            }
        });
    }
}
