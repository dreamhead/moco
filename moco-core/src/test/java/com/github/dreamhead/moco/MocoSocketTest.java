package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.junit.Before;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.socketServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.local;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoSocketTest {
    private MocoSocketHelper helper;
    private SocketServer server;

    @Before
    public void setup() {
        this.helper = new MocoSocketHelper(local(), port());
        this.server = socketServer(port());
    }

    @Test
    public void should_return_expected_response() throws Exception {
        server.request(by("foo")).response(line("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                assertThat(helper.send("foo"), is("bar"));
                helper.close();
            }
        });
    }

    @Test
    public void should_return_many_expected_responses() throws Exception {
        server.request(by("foo")).response(line("bar"));
        server.request(by("bar")).response(line("blah"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                assertThat(helper.send("foo"), is("bar"));
                assertThat(helper.send("bar"), is("blah"));
                helper.close();
            }
        });
    }

    @Test
    public void should_match_extreme_big_request() throws Exception {
        server.request(by(times("a", 1025))).response(line("long_a"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                assertThat(helper.send(times("a", 1025)), is("long_a"));
                helper.close();
            }
        });
    }

    private String times(String base, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(base);
        }
        return sb.toString();
    }

    private String line(String text) {
        return text + "\r\n";
    }
}
