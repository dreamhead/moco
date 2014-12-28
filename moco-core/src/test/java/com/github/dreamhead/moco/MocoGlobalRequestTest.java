package com.github.dreamhead.moco;

import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoGlobalRequestTest extends AbstractMocoHttpTest {
    @Test
    public void should_match_global_header() throws Exception {
        server = httpserver(port(), request(eq(header("foo"), "bar")));
        server.request(by(uri("/global-request"))).response(text("blah"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String result = helper.getWithHeader(remoteUrl("/global-request"), of("foo", "bar"));
                assertThat(result, is("blah"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_global_matcher() throws Exception {
        server = httpserver(port(), request(eq(header("foo"), "bar")));
        server.request(by(uri("/global-request"))).response(text("blah"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String result = helper.get(remoteUrl("/global-request"));
                assertThat(result, is("blah"));
            }
        });
    }

    @Test
    public void should_match_global_header_with_any_response() throws Exception {
        server = httpserver(port(), request(eq(header("foo"), "bar")));
        server.response(text("blah"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String result = helper.getWithHeader(root(), of("foo", "bar"));
                assertThat(result, is("blah"));

            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_global_matcher_for_any_response() throws Exception {
        server = httpserver(port(), request(eq(header("foo"), "bar")));
        server.response(text("blah"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(root());
            }
        });
    }
}
