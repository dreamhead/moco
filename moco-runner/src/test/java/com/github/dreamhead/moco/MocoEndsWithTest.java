package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoEndsWithTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_match_uri() throws IOException {
        runWithConfiguration("ends_with.json");
        assertThat(helper.get(remoteUrl("/bar/foo")), is("uri_match"));
        assertThat(helper.get(remoteUrl("/blah/foo")), is("uri_match"));
    }

    @Test
    public void should_match_text() throws IOException {
        runWithConfiguration("ends_with.json");
        assertThat(helper.postContent(remoteUrl("/text-match"), "barfoo"), is("text_match"));
        assertThat(helper.postContent(remoteUrl("/text-match"), "blahfoo"), is("text_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("ends_with.json");

        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of("Content-type", "application/json")), is("header_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("ends_with.json");

        assertThat(helper.get(remoteUrl("/query-match?foo=bar")), is("query_match"));
    }
}
