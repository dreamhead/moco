package com.github.dreamhead.moco.junit;

import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import com.github.dreamhead.moco.RestServer;
import org.apache.http.HttpResponse;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.MocoRest.post;
import static com.github.dreamhead.moco.MocoRest.restServer;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoJunitPojoRestRunnerTest extends AbstractMocoStandaloneTest {
    private static RestServer server;

    static {
        server = restServer(12306);
        server.resource("targets",
                post().response(status(201), header("Location", "/targets/123"))
        );
    }

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.restRunner(server);

    @Test
    public void should_return_expected_message() throws IOException {
        HttpResponse response = helper.postForResponse(remoteUrl("/targets"), "hello");
        assertThat(response.getStatusLine().getStatusCode(), is(201));
        assertThat(response.getFirstHeader("Location").getValue(), is("/targets/123"));
    }
}