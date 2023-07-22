package com.github.dreamhead.moco;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ProtocolException;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableListMultimap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoCorsStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_support_allow_origin() throws IOException, ProtocolException {
        runWithConfiguration("cors.json");
        ClassicHttpResponse response = helper.getResponseWithHeader(remoteUrl("/cors"), of("Origin", "https://www.github.com/"));
        assertThat(response.getHeader("Access-Control-Allow-Origin").getValue(), is("*"));
        assertThat(response.getHeader("Access-Control-Allow-Methods").getValue(), is("GET,POST,PUT,DELETE,OPTIONS"));
    }
}
