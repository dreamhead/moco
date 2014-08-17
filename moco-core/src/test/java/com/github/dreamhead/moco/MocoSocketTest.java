package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoSockerHelper;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.socketServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.local;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoSocketTest {
    private MocoSockerHelper helper;
    private SocketServer server;

    public MocoSocketTest() {
        this.helper = new MocoSockerHelper(local(), port());
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

    private String line(String text) {
        return text + "\r\n";
    }
}
