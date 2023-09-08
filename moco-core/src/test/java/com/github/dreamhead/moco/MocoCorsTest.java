package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableMultimap;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.MocoCors.allowCredentials;
import static com.github.dreamhead.moco.MocoCors.allowHeaders;
import static com.github.dreamhead.moco.MocoCors.allowMethods;
import static com.github.dreamhead.moco.MocoCors.allowOrigin;
import static com.github.dreamhead.moco.MocoCors.cors;
import static com.github.dreamhead.moco.MocoCors.exposeHeaders;
import static com.github.dreamhead.moco.MocoCors.maxAge;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.google.common.collect.ImmutableListMultimap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        server.response(cors(allowOrigin("https://www.github.com"), allowMethods("PUT")));

        running(server, () -> {
            ClassicHttpResponse response = helper.putForResponseWithHeaders(root(), "", of("Origin", "https://www.github.com"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("PUT"));
        });
    }

    @Test
    public void should_support_cors_with_http_methods() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com"), allowMethods(HttpMethod.PUT)));

        running(server, () -> {
            ClassicHttpResponse response = helper.putForResponseWithHeaders(root(), "", of("Origin", "https://www.github.com"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("PUT"));
        });
    }

    @Test
    public void should_support_cors_with_any_methods() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com"), allowMethods("*")));

        running(server, () -> {
            ClassicHttpResponse response = helper.putForResponseWithHeaders(root(), "", of("Origin", "https://www.github.com"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("*"));
        });
    }

    @Test
    public void should_support_cors_with_multiple_methods() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com"), allowMethods("PUT", "POST")));

        running(server, () -> {
            ClassicHttpResponse response = helper.putForResponseWithHeaders(root(), "", of("Origin", "https://www.github.com"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("PUT,POST"));
        });
    }

    @Test
    public void should_support_cors_with_matched_method() throws Exception {
        server.response(cors(allowMethods("PUT")));

        running(server, () -> {
            ClassicHttpResponse response = helper.putForResponseWithHeaders(root(), "", ImmutableMultimap.of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("PUT"));

            ClassicHttpResponse wrongResponse = helper.deleteForResponseWithHeaders(root(), of("Origin", "https://www.github.com/"));
            assertThat(wrongResponse.getHeader("Access-Control-Allow-Methods"), nullValue());
        });
    }

    @Test
    public void should_support_cors_with_headers() throws Exception {
        server.response(cors(allowHeaders("X-Header")));

        running(server, () -> {
            ClassicHttpResponse response = helper.putForResponseWithHeaders(root(), "", of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("X-Header"));
        });
    }

    @Test
    public void should_support_cors_with_multiple_headers() throws Exception {
        server.response(cors(allowHeaders("X-Header", "Y-Header")));

        running(server, () -> {
            ClassicHttpResponse response = helper.putForResponseWithHeaders(root(), "", ImmutableMultimap.of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("X-Header,Y-Header"));
        });
    }

    @Test
    public void should_support_cors_with_origin_methods_and_headers() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com"), allowMethods("PUT"), allowHeaders("X-Header")));

        running(server, () -> {
            ClassicHttpResponse response = helper.putForResponseWithHeaders(root(), "", ImmutableMultimap.of("Origin", "https://www.github.com"));
            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("https://www.github.com"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("PUT"));
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

    @Test
    public void should_support_cors_with_expose_headers() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com/"), exposeHeaders("X-Header")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));

            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Expose-Headers").getValue(), is("X-Header"));
        });
    }

    @Test
    public void should_get_preflight_response() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com/"), allowMethods("PUT"), allowHeaders("X-Header")));

        running(server, () -> {
            ClassicHttpResponse response = helper.optionsForResponse(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("PUT"));
            assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("X-Header"));
        });
    }

    @Test
    public void should_support_cors_with_max_age() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com/"), maxAge(1728000, TimeUnit.SECONDS)));

        running(server, () -> {
            ClassicHttpResponse response = helper.putForResponseWithHeaders(root(), "", of("Origin", "https://www.github.com/"));

            assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Max-Age").getValue(), is("1728000"));
        });
    }

    @Test
    public void should_fail_to_get_cors_response() throws Exception {
        server.response(cors(allowOrigin("https://www.github.com/"), allowMethods("PUT"), allowHeaders("X-Header")));

        running(server, () -> {
            ClassicHttpResponse response = helper.getResponseWithHeader(root(), of("Origin", "https://www.github.com/"));
            assertThat(response.getHeader("Access-Control-Allow-Methods"), nullValue());
        });
    }

    @Test
    public void should_not_allow_unknown_headers() {
        assertThrows(IllegalArgumentException.class, () ->
                allowMethods("foo"));
    }
}
