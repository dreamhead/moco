package com.github.dreamhead.moco;

import org.apache.http.HttpResponse;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRecordReplayStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response() throws IOException {
        runWithConfiguration("record_replay.json");
        helper.postContent(remoteUrl("/record"), "foo");
        assertThat(helper.get(remoteUrl("/replay")), is("foo"));
        helper.postContent(remoteUrl("/record"), "bar");
        assertThat(helper.get(remoteUrl("/replay")), is("bar"));
    }

    @Test
    public void should_return_expected_response_with_identifier() throws IOException {
        runWithConfiguration("record_replay.json");
        helper.postContent(remoteUrl("/record-template?type=foo"), "foo");
        helper.postContent(remoteUrl("/record-template?type=bar"), "bar");
        assertThat(helper.get(remoteUrl("/replay-template?type=foo")), is("foo"));
        assertThat(helper.get(remoteUrl("/replay-template?type=bar")), is("bar"));
    }

    @Test
    public void should_return_expected_response_with_modifier() throws IOException {
        runWithConfiguration("record_replay.json");
        helper.postContent(remoteUrl("/record-modifier?type=blah"), "foo");
        assertThat(helper.get(remoteUrl("/replay-modifier?type=blah")), is("blah"));
    }

    @Test
    public void should_return_expected_response_with_group() throws IOException {
        runWithConfiguration("record_replay.json");
        helper.postContent(remoteUrl("/record-group"), "foo");
        assertThat(helper.get(remoteUrl("/replay-group")), is("foo"));
    }

    @Test
    public void should_return_expected_response_with_tape() throws IOException {
        runWithConfiguration("record_replay.json");
        helper.postContent(remoteUrl("/record-tape"), "foo");
        assertThat(helper.get(remoteUrl("/replay-tape")), is("foo"));
    }

    @Test
    public void should_return_expected_response_with_modifier_for_header() throws IOException {
        runWithConfiguration("record_replay.json");
        helper.postContent(remoteUrl("/record-modifier-with-header?type=blah"), "foo");
        HttpResponse response = helper.getResponse(remoteUrl("/replay-modifier-with-header?type=blah"));
        assertThat(response.getFirstHeader("X-REPLAY").getValue(), is("blah"));
    }
}