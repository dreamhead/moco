package com.github.dreamhead.moco;

import com.github.dreamhead.moco.recorder.RequestRecorder;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.record;
import static com.github.dreamhead.moco.Moco.replay;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRecordTest extends AbstractMocoHttpTest {
    @Test
    public void should_record_and_replay() throws Exception {
        RequestRecorder recorder = new RequestRecorder();
        server.request(by(uri("/record"))).response(record(recorder));
        server.request(by(uri("/replay"))).response(replay(recorder));

        running(server, () -> {
            helper.postContent(remoteUrl("/record"), "foo");
            assertThat(helper.get(remoteUrl("/replay")), is("foo"));
        });
    }
}
