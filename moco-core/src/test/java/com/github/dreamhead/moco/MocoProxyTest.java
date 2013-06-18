package com.github.dreamhead.moco;

import com.google.common.io.Files;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.hamcrest.Matcher;
import org.hamcrest.core.SubstringMatcher;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
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
        server.request(and(by(uri("/target")), by(method("put")), by("proxy"))).response("put_proxy");
        server.request(and(by(uri("/target")), by(method("delete")))).response("delete_proxy");

        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/proxy")), is("get_proxy"));
                assertThat(helper.postContent(remoteUrl("/proxy"), "proxy"), is("post_proxy"));

                String putResponse = Request.Put(remoteUrl("/proxy")).bodyString("proxy", ContentType.DEFAULT_TEXT).execute().returnContent().asString();
                assertThat(putResponse, is("put_proxy"));

                String deleteResponse = Request.Delete(remoteUrl("/proxy")).execute().returnContent().asString();
                assertThat(deleteResponse, is("delete_proxy"));
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
                Content content10 = Request.Get(remoteUrl("/proxy")).version(HttpVersion.HTTP_1_0).execute().returnContent();
                assertThat(content10.asString(), is("1.0"));

                Content content11 = Request.Get(remoteUrl("/proxy")).version(HttpVersion.HTTP_1_1).execute().returnContent();
                assertThat(content11.asString(), is("1.1"));
            }
        });
    }

    @Test
    public void should_proxy_with_response_version() throws Exception {
        server.request(and(by(uri("/target")), by(version("HTTP/1.0")))).response(version("HTTP/1.0"));
        server.request(and(by(uri("/target")), by(version("HTTP/1.1")))).response(version("HTTP/1.1"));
        server.request(and(by(uri("/target")), by(version("HTTP/0.9")))).response(version("HTTP/1.0"));
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response10 = Request.Get(remoteUrl("/proxy")).version(HttpVersion.HTTP_1_0).execute().returnResponse();
                assertThat(response10.getProtocolVersion().toString(), is(HttpVersion.HTTP_1_0.toString()));

                HttpResponse response11 = Request.Get(remoteUrl("/proxy")).version(HttpVersion.HTTP_1_1).execute().returnResponse();
                assertThat(response11.getProtocolVersion().toString(), is(HttpVersion.HTTP_1_1.toString()));

                HttpResponse response09 = Request.Get(remoteUrl("/proxy")).version(HttpVersion.HTTP_0_9).execute().returnResponse();
                assertThat(response09.getProtocolVersion().toString(), is(HttpVersion.HTTP_1_0.toString()));
            }
        });
    }

    @Test
    public void should_failover_with_response_content() throws Exception {
        server.post(and(by(uri("/target")), by("proxy"))).response("proxy");
        final File tempFile = File.createTempFile("temp", ".json");
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target"), failover(tempFile.getAbsolutePath())));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(remoteUrl("/proxy"), "proxy"), is("proxy"));
                assertThat(Files.toString(tempFile, Charset.defaultCharset()), containsString("proxy"));
            }
        });
    }

    @Test
    public void should_failover_with_many_response_content() throws Exception {
        server.get(by(uri("/target"))).response("get_proxy");
        server.post(and(by(uri("/target")), by("proxy"))).response("post_proxy");

        final File tempFile = File.createTempFile("temp", ".json");
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target"), failover(tempFile.getAbsolutePath())));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/proxy")), is("get_proxy"));
                assertThat(helper.postContent(remoteUrl("/proxy"), "proxy"), is("post_proxy"));
                String failoverContent = Files.toString(tempFile, Charset.defaultCharset());
                assertThat(failoverContent, containsString("get_proxy"));
                assertThat(failoverContent, containsString("post_proxy"));
            }
        });
    }

    @Test
    public void should_failover_with_same_response_once() throws Exception {
        server.post(and(by(uri("/target")), by("proxy"))).response("0XCAFEBABE");
        final File tempFile = File.createTempFile("temp", ".json");
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target"), failover(tempFile.getAbsolutePath())));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(remoteUrl("/proxy"), "proxy"), is("0XCAFEBABE"));
                assertThat(helper.postContent(remoteUrl("/proxy"), "proxy"), is("0XCAFEBABE"));
                assertThat(Files.toString(tempFile, Charset.defaultCharset()), countString("0XCAFEBABE", 1));
            }
        });
    }

    private Matcher<String> countString(final String substring, final int targetCount) {
        return new SubstringMatcher(substring) {
            @Override
            protected boolean evalSubstringOf(String string) {
                int count = 0;
                int current = 0;

                while (current < string.length()) {
                    int index = string.indexOf(substring, current);
                    if (index > 0) {
                        count++;
                        current = index + substring.length();
                    } else {
                        break;
                    }
                }

                return count == targetCount;
            }

            @Override
            protected String relationship() {
                return format("count %d string", targetCount);
            }
        };
    }

    @Test
    public void should_failover_for_unreachable_remote_server() throws Exception {
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target"), failover("src/test/resources/failover.response")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(remoteUrl("/proxy"), "proxy"), is("proxy"));
            }
        });
    }

    @Test
    public void should_failover_for_unreachable_remote_server_with_many_content() throws Exception {
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target"), failover("src/test/resources/many_content_failover.response")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/proxy")), is("get_proxy"));
                assertThat(helper.postContent(remoteUrl("/proxy"), "proxy"), is("post_proxy"));
            }
        });
    }
}
