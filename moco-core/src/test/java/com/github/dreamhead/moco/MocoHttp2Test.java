package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.Http2TestHelper;
import com.github.dreamhead.moco.helper.RequestHeaders;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for HTTP/2 support.
 * Uses Java HttpClient which has native HTTP/2 support.
 */
public class MocoHttp2Test extends AbstractMocoHttpTest {

    @Test
    void should_send_http_get_request() throws Exception {
        server.request(by(uri("/http"))).response("HTTP OK");

        running(server, () -> {
            try (Http2TestHelper helper = new Http2TestHelper()) {
                String response = helper.get(remoteUrl("/http"));
                assertThat(response, is("HTTP OK"));
            }
        });
    }

    @Test
    void should_send_http_post_request() throws Exception {
        server.request(by(uri("/post"))).response("POST OK");

        running(server, () -> {
            try (Http2TestHelper helper = new Http2TestHelper()) {
                String response = helper.post(remoteUrl("/post"), "test data");
                assertThat(response, is("POST OK"));
            }
        });
    }

    @Test
    void should_get_full_response_with_status() throws Exception {
        server.request(by(uri("/status"))).response("Content");

        running(server, () -> {
            try (Http2TestHelper helper = new Http2TestHelper()) {
                HttpResponse<String> response = helper.getResponse(remoteUrl("/status"));
                assertThat(response.statusCode(), is(200));
                assertThat(response.body(), is("Content"));
            }
        });
    }

    @Test
    void should_send_post_with_headers() throws Exception {
        server.request(by(uri("/headers"))).response("Headers OK");

        running(server, () -> {
            try (Http2TestHelper helper = new Http2TestHelper()) {
                String response = helper.post(remoteUrl("/headers"), "data",
                        RequestHeaders.of("X-Custom", "value"));
                assertThat(response, is("Headers OK"));
            }
        });
    }
}
