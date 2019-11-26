package com.github.dreamhead.moco;

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
}
