package com.github.dreamhead.moco;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.allowCredentials;
import static com.github.dreamhead.moco.Moco.allowHeaders;
import static com.github.dreamhead.moco.Moco.allowMethods;
import static com.github.dreamhead.moco.Moco.allowOrigin;
import static com.github.dreamhead.moco.Moco.cors;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.google.common.collect.ImmutableListMultimap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoCorsTest extends AbstractMocoHttpTest {
    @Test
    public void should_support_cors() throws Exception {
        server.response(cors());

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("*"));
        });
    }

    @Test
    public void should_support_cors_without_origin() throws Exception {
        server.response(cors());

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponse(root());
            assertThat(response.getHeader("Access-Control-Allow-Origin"), nullValue());
        });
    }

    @Test
    public void should_support_cors_with_origin() throws Exception {
        server.response(cors(allowOrigin("*")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("*"));
        });
    }

    @Test
    public void should_support_cors_with_matched_origin() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com/")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("https://www.github.com/"));

            ClassicHttpResponse wrongResponse = helper.getResponseWithHeader(root(), of("Origin", "https://docs.github.com"));
            assertThat(wrongResponse.getHeader("Access-Control-Allow-Origin"), nullValue());
        });
    }

    @Test
    public void should_support_cors_with_methods() throws Exception {
        server.response(cors(allowOrigin("foo"), allowMethods("GET")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("GET"));
        });
    }

    @Test
    public void should_support_cors_with_multiple_methods() throws Exception {
        server.response(cors(allowOrigin("foo"), allowMethods("GET", "POST")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("GET,POST"));
        });
    }

    @Test
    public void should_support_cors_with_matched_method() throws Exception {
        server.response(cors(allowMethods("GET")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("GET"));

            ClassicHttpResponse wrongResponse = helper.deleteForResponseWithHeaders(root(), of("Origin", "https://www.github.com/"));
            assertThat(wrongResponse.getHeader("Access-Control-Allow-Methods"), nullValue());
        });
    }

    @Test
    public void should_support_cors_with_headers() throws Exception {
        server.response(cors(allowHeaders("X-Header")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("X-Header"));
        });
    }

    @Test
    public void should_support_cors_with_multiple_headers() throws Exception {
        server.response(cors(allowHeaders("X-Header", "Y-Header")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("X-Header,Y-Header"));
        });
    }

    @Test
    public void should_support_cors_with_origin_methods_and_headers() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com/"), allowMethods("GET"), allowHeaders("X-Header")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("GET"));
            assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("X-Header"));
        });
    }

    @Test
    public void should_support_allow_credentials() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com/"), allowCredentials(true)));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Credentials").getValue(), is("true"));
        });
    }
}
