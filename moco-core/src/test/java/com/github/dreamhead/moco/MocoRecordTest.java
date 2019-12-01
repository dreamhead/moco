package com.github.dreamhead.moco;

import com.github.dreamhead.moco.recorder.InMemoryRequestRecorder;
import com.github.dreamhead.moco.recorder.RequestRecorder;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.MocoRecorder.record;
import static com.github.dreamhead.moco.MocoRecorder.replay;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRecordTest extends AbstractMocoHttpTest {
    @Test
    public void should_record_and_replay() throws Exception {
        RequestRecorder recorder = new InMemoryRequestRecorder();
        server.request(by(uri("/record"))).response(record(recorder));
        server.request(by(uri("/replay"))).response(replay(recorder));

        running(server, () -> {
            helper.postContent(remoteUrl("/record"), "foo");
            assertThat(helper.get(remoteUrl("/replay")), is("foo"));
            helper.postContent(remoteUrl("/record"), "bar");
            assertThat(helper.get(remoteUrl("/replay")), is("bar"));
        });
    }

    @Test
    public void should_record_and_replay_with_name() throws Exception {
        server.request(by(uri("/record"))).response(record("foo"));
        server.request(by(uri("/replay"))).response(replay("foo"));

        running(server, () -> {
            helper.postContent(remoteUrl("/record"), "foo");
            assertThat(helper.get(remoteUrl("/replay")), is("foo"));
            helper.postContent(remoteUrl("/record"), "bar");
            assertThat(helper.get(remoteUrl("/replay")), is("bar"));
        });
    }

    @Test
    public void should_record_and_replay_with_template() throws Exception {
        server.request(by(uri("/record"))).response(record(template("${req.queries['type']}")));
        server.request(by(uri("/replay"))).response(replay(template("${req.queries['type']}")));

        running(server, () -> {
            helper.postContent(remoteUrl("/record?type=foo"), "foo");
            helper.postContent(remoteUrl("/record?type=bar"), "bar");
            assertThat(helper.get(remoteUrl("/replay?type=foo")), is("foo"));
            assertThat(helper.get(remoteUrl("/replay?type=bar")), is("bar"));
            assertThat(helper.get(remoteUrl("/replay?type=foo")), is("foo"));
            assertThat(helper.get(remoteUrl("/replay?type=bar")), is("bar"));
        });
    }

    @Test
    public void should_record_and_replay_with_group_and_template() throws Exception {
        server.request(by(uri("/foo-record"))).response(record("foo", template("${req.queries['type']}")));
        server.request(by(uri("/bar-record"))).response(record("bar", template("${req.queries['type']}")));
        server.request(by(uri("/foo-replay"))).response(replay("foo", template("${req.queries['type']}")));
        server.request(by(uri("/bar-replay"))).response(replay("bar", template("${req.queries['type']}")));

        running(server, () -> {
            helper.postContent(remoteUrl("/foo-record?type=blah"), "foo");
            helper.postContent(remoteUrl("/bar-record?type=blah"), "bar");
            assertThat(helper.get(remoteUrl("/foo-replay?type=blah")), is("foo"));
            assertThat(helper.get(remoteUrl("/bar-replay?type=blah")), is("bar"));
            assertThat(helper.get(remoteUrl("/foo-replay?type=blah")), is("foo"));
            assertThat(helper.get(remoteUrl("/bar-replay?type=blah")), is("bar"));
        });
    }
}
