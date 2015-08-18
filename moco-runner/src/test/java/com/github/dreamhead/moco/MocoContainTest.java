package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableMultimap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoContainTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_match_uri() throws IOException {
        runWithConfiguration("contain.json");
        assertThat(helper.get(remoteUrl("/foo/bar")), is("uri_match"));
        assertThat(helper.get(remoteUrl("/bar/foo")), is("uri_match"));
        assertThat(helper.get(remoteUrl("/bar/foo/blah")), is("uri_match"));
    }

    @Test
    public void should_match_text() throws IOException {
        runWithConfiguration("contain.json");
        assertThat(helper.postContent(remoteUrl("/text-match"), "foobar"), is("text_match"));
        assertThat(helper.postContent(remoteUrl("/text-match"), "barfoo"), is("text_match"));
        assertThat(helper.postContent(remoteUrl("/text-match"), "foobarblah"), is("text_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("contain.json");

        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of("foo", "bar/blah")), is("header_match"));
        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of("foo", "application/bar")), is("header_match"));
        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of("foo", "application/bar/blah")), is("header_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("contain.json");

        assertThat(helper.get(remoteUrl("/query-match?foo=barblah")), is("query_match"));
        assertThat(helper.get(remoteUrl("/query-match?foo=blahbar")), is("query_match"));
        assertThat(helper.get(remoteUrl("/query-match?foo=blbarah")), is("query_match"));
    }
}
