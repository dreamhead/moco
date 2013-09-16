package com.github.dreamhead.moco;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoHttpMethodTest extends AbstractMocoTest {
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
                String response = Request.Put(root()).bodyByteArray("foo".getBytes()).execute().returnContent().asString();
                assertThat(response, is("bar"));
            }
        });
    }

    @Test
    public void should_match_delete_method() throws Exception {
        server.delete(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String response = Request.Delete(remoteUrl("/foo")).execute().returnContent().asString();
                assertThat(response, is("bar"));
            }
        });
    }
}
