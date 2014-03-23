package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.httpsServer;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.RemoteTestUtils.httpsRoot;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
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
}
