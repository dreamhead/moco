package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.MocoRequestHit.once;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.RemoteTestUtils.*;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.internal.HttpsCertificate.pathCertificate;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoHttpsTest {
    protected MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
    }

    @Test
    public void should_return_expected_result() throws Exception {
        HttpsServer server = httpsServer(port(), pathCertificate("/cert.jks", "mocohttps", "mocohttps"));
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
        HttpsServer server = httpsServer(port(), pathCertificate("/cert.jks", "mocohttps", "mocohttps"));
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
        HttpsServer server = httpsServer(port(), pathCertificate("/cert.jks", "mocohttps", "mocohttps"), hit);
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
        final HttpsServer server = httpsServer(pathCertificate("/cert.jks", "mocohttps", "mocohttps"));
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
        final HttpsServer server = httpsServer(pathCertificate("/cert.jks", "mocohttps", "mocohttps"), hit);
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
        HttpsServer server = httpsServer(port(), pathCertificate("/cert.jks", "mocohttps", "mocohttps"), context("/foo"));
        server.request(by(uri("/bar"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteHttpsUrl("/foo/bar")), is("foo"));
            }
        });
    }
}
