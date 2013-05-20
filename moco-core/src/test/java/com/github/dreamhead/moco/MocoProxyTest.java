package com.github.dreamhead.moco;

import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.proxy;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoProxyTest extends AbstractMocoTest {
    @Test
    public void should_fetch_remote_url() throws Exception {
        server.response(proxy("https://github.com/"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                int statusCode = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                assertThat(statusCode, is(200));
            }
        });
    }
}
