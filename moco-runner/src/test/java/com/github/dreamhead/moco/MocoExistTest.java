package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoExistTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_exist_text() throws IOException {
        runWithConfiguration("exist.json");
        assertThat(helper.postContent(remoteUrl("/text-match"), "anything"), is("text_match"));
        assertThat(helper.postContent(remoteUrl("/text-match"), "whatever"), is("text_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("exist.json");

        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of("Content-type", "application/json")), is("header_match"));
        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of("Content-type", "application/xml")), is("header_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("exist.json");

        assertThat(helper.get(remoteUrl("/query-match?foo=bar")), is("query_match"));
        assertThat(helper.get(remoteUrl("/query-match?foo=blah")), is("query_match"));
    }
}
