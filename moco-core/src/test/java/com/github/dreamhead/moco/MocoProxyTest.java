package com.github.dreamhead.moco;

import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
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

    @Test
    public void should_proxy_with_request_method() throws Exception {
        server.get(by(uri("/target"))).response("get_proxy");
        server.post(and(by(uri("/target")), by("proxy"))).response("post_proxy");
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/proxy")), is("get_proxy"));
                assertThat(helper.postContent(remoteUrl("/proxy"), "proxy"), is("post_proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_request_header() throws Exception {
        server.request(and(by(uri("/target")), eq(header("foo"), "foo"))).response("foo_proxy");
        server.request(and(by(uri("/target")), eq(header("bar"), "bar"))).response("bar_proxy");
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Content content = Request.Get(remoteUrl("/proxy")).addHeader("foo", "foo").execute().returnContent();
                assertThat(content.asString(), is("foo_proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_request_query_parameters() throws Exception {
        server.request(and(by(uri("/target")), eq(query("foo"), "foo"))).response("foo_proxy");
        server.request(and(by(uri("/target")), eq(query("bar"), "bar"))).response("bar_proxy");
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/proxy?foo=foo")), is("foo_proxy"));
                assertThat(helper.get(remoteUrl("/proxy?bar=bar")), is("bar_proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_response_headers() throws Exception {
        server.request(and(by(uri("/target")), eq(header("foo"), "foo"))).response(header("foo", "foo_header"));
        server.request(and(by(uri("/target")), eq(header("bar"), "bar"))).response(header("bar", "bar_header"));
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String fooHeader = Request.Get(remoteUrl("/proxy")).addHeader("foo", "foo").execute().returnResponse().getHeaders("foo")[0].getValue();
                assertThat(fooHeader, is("foo_header"));
            }
        });
    }

    @Test
    public void should_proxy_with_request_version() throws Exception {
        server.request(and(by(uri("/target")), by(version("HTTP/1.0")))).response("1.0");
        server.request(and(by(uri("/target")), by(version("HTTP/1.1")))).response("1.1");
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String responseFor10 = Request.Get(remoteUrl("/proxy")).version(HttpVersion.HTTP_1_0).execute().returnContent().asString();
                assertThat(responseFor10, is("1.0"));

                String responseFor11 = Request.Get(remoteUrl("/proxy")).version(HttpVersion.HTTP_1_1).execute().returnContent().asString();
                assertThat(responseFor11, is("1.1"));
            }
        });
    }
}
