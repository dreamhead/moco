package com.github.dreamhead.moco;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
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

        Content jsonContent = Request.Get(remoteUrl("/header-match")).addHeader("Content-type", "application/json").execute().returnContent();
        assertThat(jsonContent.asString(), is("header_match"));
        Content xmlContent = Request.Get(remoteUrl("/header-match")).addHeader("Content-type", "application/xml").execute().returnContent();
        assertThat(xmlContent.asString(), is("header_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("match.json");

        assertThat(helper.get(remoteUrl("/query-match?foo=bar")), is("query_match"));
        assertThat(helper.get(remoteUrl("/query-match?foo=blah")), is("query_match"));
    }
}
