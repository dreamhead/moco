package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.apache.hc.client5.http.HttpResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoMount.to;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MocoContextTest {
    private static final String MOUNT_DIR = "src/test/resources/test";

    private HttpServer server;
    private MocoTestHelper helper;

    @BeforeEach
    public void setUp() {
        helper = new MocoTestHelper();
        server = httpServer(port(), context("/context"));
    }

    @Test
    public void should_config_context() throws Exception {
        server.get(by(uri("/foo"))).response("foo");

        running(server, () -> assertThat(helper.get(remoteUrl("/context/foo")), is("foo")));
    }

    @Test
    public void should_mount_correctly() throws Exception {
        server.mount(MOUNT_DIR, to("/dir"));

        running(server, () -> assertThat(helper.get(remoteUrl("/context/dir/dir.response")), is("response from dir")));
    }

    @Test
    public void should_have_context_even_if_there_is_no_context_configured() throws Exception {
        server.response("foo");

        running(server, () -> {
            String content = helper.get(remoteUrl("/context"));
            assertThat(content, is("foo"));
        });
    }

    @Test
    public void should_throw_exception_without_context() {
        server.request(by("foo")).response("foo");

        assertThrows(HttpResponseException.class, () -> {
            running(server, () -> helper.postContent(root(), "foo"));
        });
    }

    @Test
    public void should_throw_exception_without_context_for_any_response_handler() throws Exception {
        server.response("foo");

        assertThrows(HttpResponseException.class, () -> {
            running(server, () -> helper.get(root()));
        });
    }
}
