package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.AbstractMocoTest;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runnable;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ActualHttpServerTest extends AbstractMocoTest {
    private HttpServer httpServer;
    private HttpServer anotherServer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        httpServer = httpserver(12306, context("/foo"));
        httpServer.response("foo");
        anotherServer = httpserver(12306, context("/bar"));
    }

    @Test
    public void should_merge_http_server_with_any_handler_one_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeHttpServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo/anything")), is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_for_merging_http_server_with_any_handler_one_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeHttpServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(remoteUrl("/bar/anything"));
            }
        });
    }

    @Test
    public void should_merge_http_server_with_any_handler_other_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) httpServer).mergeHttpServer((ActualHttpServer) anotherServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo/anything")), is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_for_merging_http_server_with_any_handler_other_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) httpServer).mergeHttpServer((ActualHttpServer) anotherServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(remoteUrl("/bar/anything"));
            }
        });
    }

    @Test
    public void should_config_handler_correctly_while_merging() throws Exception {
        httpServer = httpserver(12306, fileRoot("src/test/resources"));
        httpServer.response(file("foo.response"));
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeHttpServer((ActualHttpServer) httpServer);

        running(mergedServer, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    @Test
    public void should_config_handler_correctly_other_side_while_merging() throws Exception {
        httpServer = httpserver(12306, fileRoot("src/test/resources"));
        httpServer.response(file("foo.response"));
        HttpServer mergedServer = ((ActualHttpServer) httpServer).mergeHttpServer((ActualHttpServer) anotherServer);

        running(mergedServer, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }
}
