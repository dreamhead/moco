package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoMount.to;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoContextTest {
    private static final String MOUNT_DIR = "src/test/resources/test";

    private HttpServer server;
    private MocoTestHelper helper;

    @Before
    public void setUp() {
        helper = new MocoTestHelper();
        server = httpServer(port(), context("/context"));
    }

    @Test
    public void should_config_context() throws Exception {
        server.get(by(uri("/foo"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/context/foo")), is("foo"));
            }
        });
    }

    @Test
    public void should_mount_correctly() throws Exception {
        server.mount(MOUNT_DIR, to("/dir"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/context/dir/dir.response")), is("response from dir"));
            }
        });
    }

    @Test
    public void should_have_context_even_if_there_is_no_context_configured() throws Exception {
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String content = helper.get(remoteUrl("/context"));
                assertThat(content, is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_context() throws Exception {
        server.request(by("foo")).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.postContent(root(), "foo");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_context_for_any_response_handler() throws Exception {
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(root());
            }
        });
    }
}
