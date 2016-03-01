package com.github.dreamhead.moco;

import com.google.common.io.Files;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.hamcrest.Matcher;
import org.hamcrest.core.SubstringMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.dreamhead.moco.HttpProtocolVersion.*;
import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.MocoRequestHit.once;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.*;
import static com.github.dreamhead.moco.Runner.running;
import static com.google.common.collect.ImmutableMultimap.of;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class MocoProxyTest extends AbstractMocoHttpTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void should_fetch_remote_url() throws Exception {
        server.response(proxy("https://github.com/"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getForStatus(root()), is(200));
            }
        });
    }

    @Test
    public void should_proxy_with_request_method() throws Exception {
        server.get(by(uri("/target"))).response("get_proxy");
        server.post(and(by(uri("/target")), by("proxy"))).response("post_proxy");
        server.request(and(by(uri("/target")), by(method("put")), by("proxy"))).response("put_proxy");
        server.request(and(by(uri("/target")), by(method("delete")))).response("delete_proxy");
        server.request(and(by(uri("/target")), by(method("head")))).response(status(200));
        server.request(and(by(uri("/target")), by(method("options")))).response("options_proxy");
        server.request(and(by(uri("/target")), by(method("trace")))).response("trace_proxy");

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

                StatusLine headStatusLine = Request.Head(remoteUrl("/proxy")).execute().returnResponse().getStatusLine();
                assertThat(headStatusLine.getStatusCode(), is(200));

                String optionsResponse = Request.Options(remoteUrl("/proxy")).execute().returnContent().asString();
                assertThat(optionsResponse, is("options_proxy"));

                String traceResponse = Request.Trace(remoteUrl("/proxy")).execute().returnContent().asString();
                assertThat(traceResponse, is("trace_proxy"));
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
                assertThat(helper.getWithHeader(remoteUrl("/proxy"), of("foo", "foo")), is("foo_proxy"));
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
                String fooHeader = Request.Get(remoteUrl("/proxy")).addHeader("foo", "foo").execute().returnResponse().getFirstHeader("foo").getValue();
                assertThat(fooHeader, is("foo_header"));
            }
        });
    }

    @Test
    public void should_proxy_with_request_version() throws Exception {
        server.request(and(by(uri("/target")), by(version(VERSION_1_0)))).response("1.0");
        server.request(and(by(uri("/target")), by(version(VERSION_1_1)))).response("1.1");
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getWithVersion(remoteUrl("/proxy"), HttpVersion.HTTP_1_0), is("1.0"));
                assertThat(helper.getWithVersion(remoteUrl("/proxy"), HttpVersion.HTTP_1_1), is("1.1"));
            }
        });
    }

    @Test
    public void should_proxy_with_response_version() throws Exception {
        server.request(and(by(uri("/target")), by(version(VERSION_1_0)))).response(version(VERSION_1_0));
        server.request(and(by(uri("/target")), by(version(VERSION_1_1)))).response(version(VERSION_1_1));
        server.request(and(by(uri("/target")), by(version(VERSION_0_9)))).response(version(VERSION_1_0));
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
        final File tempFile = tempFolder.newFile();
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

        final File tempFile = tempFolder.newFile();
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
        server = httpServer(port(), log());
        server.post(and(by(uri("/target")), by("proxy"))).response("0XCAFEBABE");
        final File tempFile = tempFolder.newFile();
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

//    @Test
//    public void should_be_able_to_connect_to_external_website_successfully() throws Exception {
//        server.response(proxy("http://www.baidu.com/"));
//
//        running(server, new Runnable() {
//            @Override
//            public void run() throws IOException {
//                assertThat(helper.getForStatus(root()), is(200));
//            }
//        });
//    }

    @Test
    public void should_proxy_a_batch_of_urls() throws Exception {
        server.get(by(uri("/target/1"))).response("target_1");
        server.get(by(uri("/target/2"))).response("target_2");

        server.get(match(uri("/proxy/.*"))).response(proxy(from("/proxy").to(remoteUrl("/target"))));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/proxy/1")), is("target_1"));
                assertThat(helper.get(remoteUrl("/proxy/2")), is("target_2"));
            }
        });
    }

    @Test
    public void should_proxy_a_batch_of_urls_with_failover() throws Exception {
        server.request(match(uri("/proxy/.*"))).response(proxy(from("/proxy").to(remoteUrl("/target")), failover("src/test/resources/failover.response")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(remoteUrl("/proxy/1"), "proxy"), is("proxy"));
            }
        });
    }

    @Test
    public void should_batch_proxy_from_server() throws Exception {
        server.get(by(uri("/target/1"))).response("target_1");
        server.get(by(uri("/target/2"))).response("target_2");
        server.proxy(from("/proxy").to(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/proxy/1")), is("target_1"));
                assertThat(helper.get(remoteUrl("/proxy/2")), is("target_2"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_proxy_url_for_unmatching_url_for_batch_proxy_from_server() throws Exception {
        server.proxy(from("/proxy").to(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(remoteUrl("/proxy1/1"));
            }
        });
    }

    @Test
    public void should_batch_proxy_from_server_with_context_server() throws Exception {
        server = httpServer(port(), context("/proxy"));
        server.get(by(uri("/target/1"))).response("target_1");
        server.get(by(uri("/target/2"))).response("target_2");
        server.proxy(from("/proxy").to(remoteUrl("/proxy/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/proxy/proxy/1")), is("target_1"));
                assertThat(helper.get(remoteUrl("/proxy/proxy/2")), is("target_2"));
            }
        });
    }

    @Test
    public void should_proxy_a_batch_of_urls_with_failover_from_server() throws Exception {
        server.proxy(from("/proxy").to(remoteUrl("/target")), failover("src/test/resources/failover.response"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(remoteUrl("/proxy/1"), "proxy"), is("proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_playback() throws Exception {
        server.request(by(uri("/target"))).response("proxy");
        final File file = tempFolder.newFile();
        server.request(by(uri("/proxy_playback"))).response(proxy(remoteUrl("/target"), playback(file.getAbsolutePath())));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/proxy_playback")), is("proxy"));
            }
        });
    }

    @Test
    public void should_proxy_with_playback_to_access_remote_only_once() throws Exception {
        RequestHit hit = requestHit();
        server = httpServer(port(), hit);
        server.request(by(uri("/target"))).response("proxy");
        final File file = tempFolder.newFile();
        server.request(by(uri("/proxy_playback"))).response(proxy(remoteUrl("/target"), playback(file.getAbsolutePath())));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/proxy_playback")), is("proxy"));
                assertThat(helper.get(remoteUrl("/proxy_playback")), is("proxy"));
            }
        });

        hit.verify(by(uri("/target")), once());
    }

    @Test
    public void should_ignore_some_header_from_remote_server() throws Exception {
        server.request(by(uri("/target"))).response(with("proxy"), header("Date", "2014-5-1"), header("Server", "moco"));
        server.request(by(uri("/proxy"))).response(proxy(remoteUrl("/target")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = Request.Get(remoteUrl("/proxy")).execute().returnResponse();
                assertThat(response.getFirstHeader("Date"), nullValue());
                assertThat(response.getFirstHeader("Server"), nullValue());
            }
        });
    }

    @Test
    public void should_work_well_for_chunk_response() throws Exception {
        final File file = tempFolder.newFile();
        HttpServer server = httpServer(12306, context("/"));
        server.get(match(uri("/repos/.*")))
                .response(proxy(from("/repos").to("https://api.github.com/repos"), playback(file.getAbsolutePath())));
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String result = helper.get("http://localhost:12306/repos/HipByte/RubyMotion/contributors");
                assertThat(result.isEmpty(), is(false));
            }
        });
    }
}
