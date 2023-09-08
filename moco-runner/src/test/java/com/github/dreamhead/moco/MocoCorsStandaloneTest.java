package com.github.dreamhead.moco;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ProtocolException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableListMultimap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoCorsStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_support_default_cors() throws IOException, ProtocolException {
        runWithConfiguration("cors.json");
        ClassicHttpResponse response = helper.putForResponseWithHeaders(remoteUrl("/default-cors"), "", of("Origin", "https://www.github.com/"));
        assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("*"));
        assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("*"));
        assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("*"));
    }
    @Test
    public void should_support_cors() throws IOException, ProtocolException {
        runWithConfiguration("cors.json");
        ClassicHttpResponse response = helper.putForResponseWithHeaders(remoteUrl("/cors"), "", of("Origin", "https://www.github.com/"));
        assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("*"));
        assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("GET,POST,PUT,DELETE,OPTIONS"));
        assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("Content-Type, Authorization, Accept, X-Requested-With"));
        assertThat(response.getHeader("Access-Control-Max-Age").getValue(), is("1728000"));
    }

    @Test
    public void should_support_cors_alias() throws IOException, ProtocolException {
        runWithConfiguration("cors.json");
        ClassicHttpResponse response = helper.putForResponseWithHeaders(remoteUrl("/cors-alias"), "", of("Origin", "https://www.github.com/"));
        assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("*"));
        assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("GET,POST,PUT,DELETE,OPTIONS"));
        assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("Content-Type, Authorization, Accept, X-Requested-With"));
        assertThat(response.getHeader("Access-Control-Max-Age").getValue(), is("1728000"));
    }

    @Test
    public void should_support_simple_request() throws ProtocolException, IOException {
        runWithConfiguration("cors.json");
        ClassicHttpResponse response = helper.getResponseWithHeader(remoteUrl("/cors"), of("Origin", "https://www.github.com/"));
        assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("*"));
        assertThat(response.getHeader("Access-Control-Expose-Headers").getValue(), is("X-My-Custom-Header, X-Another-Custom-Header"));
        assertThat(response.getHeader("Access-Control-Allow-Credentials").getValue(), is("true"));
    }

    @Test
    public void should_support_options() throws ProtocolException, IOException {
        runWithConfiguration("cors.json");
        ClassicHttpResponse response = helper.optionsForResponse(remoteUrl("/cors"), of("Origin", "https://www.github.com/"));
        assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("*"));
        assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("GET,POST,PUT,DELETE,OPTIONS"));
        assertThat(response.getHeader("Access-Control-Allow-Headers").getValue(), is("Content-Type, Authorization, Accept, X-Requested-With"));
        assertThat(response.getHeader("Access-Control-Max-Age").getValue(), is("1728000"));
    }
}
