package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.Http2TestHelper;
import com.github.dreamhead.moco.helper.RequestHeaders;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.httpsServer;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.httpsRoot;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoHttps2Test {
    private static final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");

    @Test
    void should_send_https_get_request() throws Exception {
        HttpsServer server = httpsServer(DEFAULT_CERTIFICATE);
        server.request(by(uri("/https"))).response("HTTPS OK");

        running(server, () -> {
            try (Http2TestHelper helper = new Http2TestHelper(true)) {
                String response = helper.get(httpsRoot(server.port()) + "/https");
                assertThat(response, is("HTTPS OK"));
            }
        });
    }

    @Test
    void should_send_https_post_request() throws Exception {
        HttpsServer server = httpsServer(DEFAULT_CERTIFICATE);
        server.request(by(uri("/secure-post"))).response("Secure Post OK");

        running(server, () -> {
            try (Http2TestHelper helper = new Http2TestHelper(true)) {
                String response = helper.post(httpsRoot(server.port()) + "/secure-post", "secure data");
                assertThat(response, is("Secure Post OK"));
            }
        });
    }

    @Test
    void should_get_https_full_response() throws Exception {
        HttpsServer server = httpsServer(DEFAULT_CERTIFICATE);
        server.request(by(uri("/secure-status"))).response("Secure Content");

        running(server, () -> {
            try (Http2TestHelper helper = new Http2TestHelper(true)) {
                HttpResponse<String> response = helper.getResponse(httpsRoot(server.port()) + "/secure-status");
                assertThat(response.statusCode(), is(200));
                assertThat(response.body(), is("Secure Content"));
            }
        });
    }

    @Test
    void should_send_https_post_with_headers() throws Exception {
        HttpsServer server = httpsServer(DEFAULT_CERTIFICATE);
        server.request(by(uri("/secure-headers"))).response("Secure Headers OK");

        running(server, () -> {
            try (Http2TestHelper helper = new Http2TestHelper(true)) {
                String response = helper.post(httpsRoot(server.port()) + "/secure-headers", "secure data",
                        RequestHeaders.of("X-Custom", "secure-value"));
                assertThat(response, is("Secure Headers OK"));
            }
        });
    }
}
