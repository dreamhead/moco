package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.Moco.socketServer;
import static com.github.dreamhead.moco.MocoRequestHit.once;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.local;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.google.common.io.Files.asCharSource;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoSocketTest {
    private MocoSocketHelper helper;
    private SocketServer server;

    @BeforeEach
    public void setup() {
        this.helper = new MocoSocketHelper(local(), port());
        this.server = socketServer(port());
    }

    @Test
    public void should_return_expected_response() throws Exception {
        server.request(by("foo")).response(line("bar"));

        running(server, () -> {
            helper.connect();
            assertThat(helper.send("foo"), is("bar"));
            helper.close();
        });
    }

    @Test
    public void should_return_many_expected_responses() throws Exception {
        server.request(by("foo")).response(line("bar"));
        server.request(by("bar")).response(line("blah"));

        running(server, () -> {
            helper.connect();
            assertThat(helper.send("foo"), is("bar"));
            assertThat(helper.send("bar"), is("blah"));
            helper.close();
        });
    }

    @Test
    public void should_match_extreme_big_request() throws Exception {
        server.request(by(times("a", 1025))).response(line("long_a"));

        running(server, () -> {
            helper.connect();
            assertThat(helper.send(times("a", 1025)), is("long_a"));
            helper.close();
        });
    }

    @Test
    public void should_log_request_and_response_into_file(@TempDir final Path path) throws Exception {
        File file = path.resolve("tempfile").toFile();
        SocketServer socketServer = socketServer(port(), log(file.getAbsolutePath()));
        socketServer.request(by("0XCAFE")).response(line("0XBABE"));

        running(socketServer, () -> {
            helper.connect();
            assertThat(helper.send("0XCAFE"), is("0XBABE"));
            helper.close();
        });

        String actual = asCharSource(file, Charset.defaultCharset()).read();
        assertThat(actual, containsString("0XBABE"));
        assertThat(actual, containsString("0XCAFE"));
    }

    @Test
    public void should_monitor_socket_server_behavior() throws Exception {
        RequestHit hit = requestHit();
        SocketServer socketServer = socketServer(port(), hit);
        socketServer.request(by("0XCAFE")).response(line("0XBABE"));

        running(socketServer, () -> {
            helper.connect();
            assertThat(helper.send("0XCAFE"), is("0XBABE"));
            helper.close();
        });

        hit.verify(by("0XCAFE"), once());
    }

    @Test
    public void should_create_socket_server_without_specific_port() throws Exception {
        final SocketServer socketServer = socketServer();
        socketServer.request(by("foo")).response(line("bar"));

        running(socketServer, () -> {
            helper = new MocoSocketHelper(local(), socketServer.port());
            helper.connect();
            assertThat(helper.send("foo"), is("bar"));
            helper.close();
        });
    }

    @Test
    public void should_verify_expected_request_and_log_at_same_time() throws Exception {
        RequestHit hit = requestHit();
        final SocketServer socketServer = socketServer(port(), hit, log());
        socketServer.request(by("foo")).response(line("bar"));

        running(socketServer, () -> {
            helper = new MocoSocketHelper(local(), socketServer.port());
            helper.connect();
            assertThat(helper.send("foo"), is("bar"));
            helper.close();
        });

        hit.verify(by("foo"), once());
    }

    private String times(final String base, final int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(base);
        }
        return sb.toString();
    }

    private String line(final String text) {
        return text + "\r\n";
    }
}
