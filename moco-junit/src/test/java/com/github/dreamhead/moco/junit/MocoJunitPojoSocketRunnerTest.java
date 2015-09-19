package com.github.dreamhead.moco.junit;

import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.helper.MocoSocketHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.socketServer;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.local;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJunitPojoSocketRunnerTest {
    private static SocketServer server;

    static {
        server = socketServer(12306);
        server.response("bar\n");
    }

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.socketRunner(server);

    private MocoSocketHelper helper;

    @Before
    public void setup() {
        this.helper = new MocoSocketHelper(local(), port());
    }

    @Test
    public void should_return_expected_message() throws IOException {
        helper.connect();
        assertThat(helper.send("foo"), is("bar"));
        helper.close();
    }
}
