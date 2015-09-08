package com.github.dreamhead.moco.junit;

import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import com.github.dreamhead.moco.HttpServer;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJunitPojoHttpRunnerTest extends AbstractMocoStandaloneTest {
    private static HttpServer server;

    static {
        server = httpServer(12306);
        server.response("foo");
    }

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.httpRunner(server);

    @Test
    public void should_return_expected_message() throws IOException {
        assertThat(helper.get(root()), is("foo"));
    }
}
