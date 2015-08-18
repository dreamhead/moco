package com.github.dreamhead.moco;

import com.google.common.net.HttpHeaders;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableMultimap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoMatchTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_match_uri() throws IOException {
        runWithConfiguration("match.json");
        assertThat(helper.get(remoteUrl("/bar/foo")), is("uri_match"));
        assertThat(helper.get(remoteUrl("/blah/foo")), is("uri_match"));
    }

    @Test
    public void should_match_text() throws IOException {
        runWithConfiguration("match.json");
        assertThat(helper.postContent(remoteUrl("/text-match"), "barfoo"), is("text_match"));
        assertThat(helper.postContent(remoteUrl("/text-match"), "blahfoo"), is("text_match"));
    }

    @Test
    public void should_match_method() throws IOException {
        runWithConfiguration("match.json");
        assertThat(helper.get(remoteUrl("/method-match")), is("method_match"));
        assertThat(helper.postContent(remoteUrl("/method-match"), "blah"), is("method_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("match.json");

        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of(HttpHeaders.CONTENT_TYPE, "application/json")), is("header_match"));
        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of(HttpHeaders.CONTENT_TYPE, "application/xml")), is("header_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("match.json");

        assertThat(helper.get(remoteUrl("/query-match?foo=bar")), is("query_match"));
        assertThat(helper.get(remoteUrl("/query-match?foo=blah")), is("query_match"));
    }
}
