package com.github.dreamhead.moco;

import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoHttpMethodTest extends AbstractMocoHttpTest {
    @Test
    public void should_match_get_method() throws Exception {
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });
    }

    @Test
    public void should_match_post_method() throws Exception {
        server.post(by("foo")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "foo"), is("bar"));
            }
        });
    }

    @Test
    public void should_match_put_method() throws Exception {
        server.put(by("foo")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Put(root()).bodyByteArray("foo".getBytes());
                assertThat(helper.executeAsString(request), is("bar"));
            }
        });
    }

    @Test
    public void should_match_delete_method() throws Exception {
        server.delete(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Delete(remoteUrl("/foo"));
                String response = helper.executeAsString(request);
                assertThat(response, is("bar"));
            }
        });
    }
}
