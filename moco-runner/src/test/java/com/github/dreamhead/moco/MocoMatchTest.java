package com.github.dreamhead.moco;

import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
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
}
