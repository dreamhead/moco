package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.junit.Before;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.MocoJsonRunner.jsonSocketServer;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.local;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJsonSocketRunnerTest {
    private MocoSocketHelper helper;

    @Before
    public void setup() {
        this.helper = new MocoSocketHelper(local(), port());
    }

    @Test
    public void should_return_expected_response() throws Exception {
        final SocketServer server = jsonSocketServer(port(), file("src/test/resources/base.json"));
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.connect();
                assertThat(helper.send("foo", 3), is("bar"));
                assertThat(helper.send("anything", 4), is("blah"));
                helper.close();
            }
        });
    }

    @Test
    public void should_return_expected_response_without_port() throws Exception {
        final SocketServer server = jsonSocketServer(file("src/test/resources/base.json"));
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                MocoSocketHelper mocoSocketHelper = new MocoSocketHelper(local(), server.port());
                mocoSocketHelper.connect();
                assertThat(mocoSocketHelper.send("foo", 3), is("bar"));
                mocoSocketHelper.close();
            }
        });
    }
}
