package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoRedirectTest {
    private HttpServer server;
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        server = httpserver(port());
        helper = new MocoTestHelper();
    }

    @Test
    public void should_redirect_to_expected_url() throws Exception {
        server.get(by(uri("/"))).response("foo");
        server.get(by(uri("/redirectTo"))).redirectTo(root());

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/redirectTo")), is("foo"));
            }
        });
}
}
