package com.github.dreamhead.moco;


import com.google.common.net.HttpHeaders;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableMultimap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoRuleTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_match_uri() throws IOException {
        runWithConfiguration("rule.json");
        assertThat(helper.get(remoteUrl("/foo/bar")), is("uri_match"));
        assertThat(helper.get(remoteUrl("/foo/blah")), is("uri_match"));
    }

    @Test
    public void should_match_text() throws IOException {
        runWithConfiguration("rule.json");
        assertThat(helper.postContent(remoteUrl("/text-match"), "foobar"), is("text_match"));
        assertThat(helper.postContent(remoteUrl("/text-match"), "fooblah"), is("text_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("rule.json");

        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of(HttpHeaders.CONTENT_TYPE, "application/json")), is("header_match"));
        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of(HttpHeaders.CONTENT_TYPE, "application/xml")), is("header_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("rule.json");

        assertThat(helper.get(remoteUrl("/query-match?foo=bar")), is("query_match"));
        assertThat(helper.get(remoteUrl("/query-match?foo=blah")), is("query_match"));
    }

    @Test
    public void should_match_json() throws IOException {
        runWithConfiguration("rule.json");

        assertThat(helper.postContent(remoteUrl("/json-match"), "{\n\t\"name\":\"Linus\"\n}"), is("json_match"));
        assertThat(helper.postContent(remoteUrl("/json-match"), "{\n\t\"name\":\"linus\"\n}"), is("json_match"));
    }
}
