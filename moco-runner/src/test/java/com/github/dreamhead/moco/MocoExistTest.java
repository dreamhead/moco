package com.github.dreamhead.moco;

import com.google.common.net.HttpHeaders;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.google.common.collect.ImmutableMultimap.of;
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
    public void should_not_exist_text() throws IOException {
        runWithConfiguration("exist.json");
        assertThat(helper.get(remoteUrl("/text-not-match")), is("text_not_match"));
    }

    @Test
    public void should_match_header() throws IOException {
        runWithConfiguration("exist.json");

        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of(HttpHeaders.CONTENT_TYPE, "application/json")), is("header_match"));
        assertThat(helper.getWithHeader(remoteUrl("/header-match"), of(HttpHeaders.CONTENT_TYPE, "application/xml")), is("header_match"));
    }

    @Test
    public void should_not_match_header() throws IOException {
        runWithConfiguration("exist.json");

        assertThat(helper.get(remoteUrl("/header-not-match")), is("header_not_match"));
    }

    @Test
    public void should_match_query() throws IOException {
        runWithConfiguration("exist.json");

        assertThat(helper.get(remoteUrl("/query-not-match")), is("query_not_match"));
    }
}
