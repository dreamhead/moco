package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.AbstractMocoTest;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runnable;
import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ActualHttpServerTest extends AbstractMocoTest {
    @Test(expected = HttpResponseException.class)
    public void should_merge_http_server_correctly() throws Exception {
        HttpServer httpServer = httpserver(12306, context("/foo"));
        httpServer.response("foo");

        HttpServer anotherServer = httpserver(12306, context("/bar"));
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeHttpServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo/anything")), is("foo"));
                helper.get(remoteUrl("/bar/anything"));
            }
        });
    }
}
