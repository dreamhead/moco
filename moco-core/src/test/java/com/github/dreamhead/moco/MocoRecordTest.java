package com.github.dreamhead.moco;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.fileRoot;
import static com.github.dreamhead.moco.Moco.group;
import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoRecorders.identifier;
import static com.github.dreamhead.moco.MocoRecorders.modifier;
import static com.github.dreamhead.moco.MocoRecorders.record;
import static com.github.dreamhead.moco.MocoRecorders.replay;
import static com.github.dreamhead.moco.MocoRecorders.tape;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoRecordTest extends AbstractMocoHttpTest {
    @Test
    public void should_record_and_replay_with_name() throws Exception {
        server.request(by(uri("/record"))).response(record(group("foo")));
        server.request(by(uri("/replay"))).response(replay(group("foo")));

        running(server, () -> {
            helper.postContent(remoteUrl("/record"), "foo");
            assertThat(helper.get(remoteUrl("/replay")), is("foo"));
            helper.postContent(remoteUrl("/record"), "bar");
            assertThat(helper.get(remoteUrl("/replay")), is("bar"));
        });
    }

    @Test
    public void should_record_and_replay_with_template() throws Exception {
        server.request(by(uri("/record"))).response(record(identifier("${req.queries['type']}")));
        server.request(by(uri("/replay"))).response(replay(identifier("${req.queries['type']}")));

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
        server.request(by(uri("/foo-record"))).response(record(group("foo"), identifier("${req.queries['type']}")));
        server.request(by(uri("/bar-record"))).response(record(group("bar"), identifier("${req.queries['type']}")));
        server.request(by(uri("/foo-replay"))).response(replay(group("foo"), identifier("${req.queries['type']}")));
        server.request(by(uri("/bar-replay"))).response(replay(group("bar"), identifier("${req.queries['type']}")));

        running(server, () -> {
            helper.postContent(remoteUrl("/foo-record?type=blah"), "foo");
            helper.postContent(remoteUrl("/bar-record?type=blah"), "bar");
            assertThat(helper.get(remoteUrl("/foo-replay?type=blah")), is("foo"));
            assertThat(helper.get(remoteUrl("/bar-replay?type=blah")), is("bar"));
            assertThat(helper.get(remoteUrl("/foo-replay?type=blah")), is("foo"));
            assertThat(helper.get(remoteUrl("/bar-replay?type=blah")), is("bar"));
        });
    }

    @Test
    public void should_record_and_replay_with_tape() throws Exception {
        File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        server.request(by(uri("/foo-record"))).response(record(group("foo"), tape(temp.getPath()), identifier("${req.queries['type']}")));
        server.request(by(uri("/foo-replay"))).response(replay(group("foo"), tape(temp.getPath()), identifier("${req.queries['type']}")));

        running(server, () -> {
            helper.postContent(remoteUrl("/foo-record?type=blah"), "foo");
            assertThat(helper.get(remoteUrl("/foo-replay?type=blah")), is("foo"));
        });

        HttpServer newServer = createServer(port());

        newServer.request(by(uri("/tape-replay"))).response(replay(group("foo"), tape(temp.getPath()), identifier("${req.queries['type']}")));

        running(newServer, () -> assertThat(helper.get(remoteUrl("/tape-replay?type=blah")), is("foo")));
    }

    @Test
    public void should_record_and_replay_with_tape_for_multiple_record() throws Exception {
        File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        server.request(by(uri("/foo-record"))).response(record(group("foo"), tape(temp.getPath()), identifier("${req.queries['type']}")));
        server.request(by(uri("/foo-replay"))).response(replay(group("foo"), tape(temp.getPath()), identifier("${req.queries['type']}")));

        running(server, () -> {
            helper.postContent(remoteUrl("/foo-record?type=bar"), "bar");
            helper.postContent(remoteUrl("/foo-record?type=blah"), "blah");
        });

        HttpServer newServer = createServer(port());

        newServer.request(by(uri("/tape-replay"))).response(replay(group("foo"), tape(temp.getPath()), identifier("${req.queries['type']}")));

        running(newServer, () -> {
            assertThat(helper.get(remoteUrl("/tape-replay?type=bar")), is("bar"));
            assertThat(helper.get(remoteUrl("/tape-replay?type=blah")), is("blah"));
        });
    }

    @Test
    public void should_do_well_for_replay_without_record() throws Exception {
        File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        server.request(by(uri("/foo-record"))).response(record(group("foo"), tape(temp.getPath()), identifier("${req.queries['type']}")));
        server.request(by(uri("/foo-replay"))).response(replay(group("foo"), tape(temp.getPath()), identifier("${req.queries['type']}")));

        running(server, () -> {
            assertThat(helper.getForStatus(remoteUrl("/tape-replay?type=bar")), is(400));
            assertThat(helper.getForStatus(remoteUrl("/tape-replay?type=blah")), is(400));
        });
    }

    @Test
    public void should_record_and_replay_with_group_and_modifier() throws Exception {
        server.request(by(uri("/foo-record"))).response(record(group("foo"), identifier("${req.queries['type']}")));
        server.request(by(uri("/bar-record"))).response(record(group("foo"), identifier("${req.queries['type']}")));
        server.request(by(uri("/foo-replay"))).response(replay(group("foo"),
                identifier("${req.queries['type']}"),
                modifier("${req.queries['type']}")));
        server.request(by(uri("/bar-replay"))).response(replay(group("foo"),
                identifier("${req.queries['type']}"),
                modifier("${req.queries['type']}")));

        running(server, () -> {
            helper.postContent(remoteUrl("/foo-record?type=blah"), "foo");
            helper.postContent(remoteUrl("/bar-record?type=blah"), "bar");
            assertThat(helper.get(remoteUrl("/foo-replay?type=blah")), is("blah"));
            assertThat(helper.get(remoteUrl("/bar-replay?type=blah")), is("blah"));
            assertThat(helper.get(remoteUrl("/foo-replay?type=blah")), is("blah"));
            assertThat(helper.get(remoteUrl("/bar-replay?type=blah")), is("blah"));
        });
    }

    @Test
    public void should_record_and_replay_with_group_and_response_handler_modifier() throws Exception {
        server.request(by(uri("/record"))).response(record(group("foo")));
        server.request(by(uri("/replay"))).response(replay(
                group("foo"),
                modifier(template("${req.content}"),
                        header("X-REPLAY", template("${req.queries['type']}")))
        ));

        running(server, () -> {
            helper.postContent(remoteUrl("/record?type=blah"), "foo");
            ClassicHttpResponse response = helper.getResponse(remoteUrl("/replay"));
            assertThat(response.getFirstHeader("X-REPLAY").getValue(), is("blah"));
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            response.getEntity().writeTo(outstream);
            assertThat(outstream.toString(), is("foo"));
        });
    }

    @Test
    public void should_record_and_replay_with_global_file_root() throws Exception {
        server = httpServer(port(), fileRoot("src/test/resources"));
        server.request(by(uri("/record"))).response(record(group("foo")));
        server.request(by(uri("/replay"))).response(replay(
                group("foo"),
                modifier(template("${req.content}"),
                        header("X-REPLAY", template(file("foo.template"))))
        ));

        running(server, () -> {
            helper.postContent(remoteUrl("/record?type=blah"), "foo");
            ClassicHttpResponse response = helper.getResponse(remoteUrl("/replay"));
            assertThat(response.getFirstHeader("X-REPLAY").getValue(), is("POST"));
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            response.getEntity().writeTo(outstream);
            assertThat(outstream.toString(), is("foo"));
        });
    }
}
