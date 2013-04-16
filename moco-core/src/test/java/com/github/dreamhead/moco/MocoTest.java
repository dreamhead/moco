package com.github.dreamhead.moco;

import com.github.dreamhead.moco.handler.ContentHandler;
import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class MocoTest extends AbstractMocoTest {
    @Test
    public void should_return_expected_response() throws Exception {
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(root()), is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_with_text_api() throws Exception {
        server.response(text("foo"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_from_file() throws Exception {
        server.response(file("src/test/resources/foo.response"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_expected_response_from_classpath_file() throws Exception {
        server.response(classpathFile("foo.response"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_for_unknown_request() throws Exception {
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_request() throws Exception {
        server.request(by("foo")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "foo"), is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_request_with_text_api() throws Exception {
        server.request(by(text("foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(root(), "foo"), is("bar"));
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_uri() throws Exception {
        server.request(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_multiple_matchers() throws Exception {
        server.request(and(by("foo"), by(uri("/foo")))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postContent(remoteUrl("/foo"), "foo"), is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_even_if_match_one_of_conditions() throws Exception {
        server.request(and(by("foo"), by(uri("/foo")))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(remoteUrl("/foo"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_either_matcher() throws Exception {
        server.request(or(by("foo"), by(uri("/foo")))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.postContent(remoteUrl("/foo"), "foo"), is("bar"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_simplified_either_matcher() throws Exception {
        server.request(by("foo"), by(uri("/foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.postContent(root(), "foo"), is("bar"));
            }
        });
    }

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
    public void should_match_get_method_by_method_api() throws Exception {
        server.request(and(by(uri("/foo")), by(method("get")))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_response_for_get_while_http_method_is_not_get() throws Exception {
        server.get(by(uri("/foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(remoteUrl("/foo"), "");
            }
        });
    }

    @Test
    public void should_match_put_method_via_api() throws Exception {
        server.request(and(by(uri("/foo")), by(method("put")))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String response = Request.Put(remoteUrl("/foo")).execute().returnContent().toString();
                assertThat(response, is("bar"));
            }
        });
    }

    @Test
    public void should_match_delete_method_via_api() throws Exception {
        server.request(and(by(uri("/foo")), by(method("delete")))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String response = Request.Delete(remoteUrl("/foo")).execute().returnContent().toString();
                assertThat(response, is("bar"));
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

    @Test(expected = HttpResponseException.class)
    public void should_not_response_for_post_while_http_method_is_not_post() throws Exception {
        server.post(by(uri("/foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(remoteUrl("/foo"));
            }
        });
    }

    @Test
    public void should_return_content_one_by_one() throws Exception {
        server.request(by(uri("/foo"))).response(seq("bar", "blah"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("blah"));
                assertThat(helper.get(remoteUrl("/foo")), is("blah"));
            }
        });
    }

    @Test
    public void should_return_content_one_by_one_with_text_api() throws Exception {
        server.request(by(uri("/foo"))).response(seq(text("bar"), text("blah")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("blah"));
                assertThat(helper.get(remoteUrl("/foo")), is("blah"));
            }
        });
    }

    @Test
    public void should_match() throws Exception {
        server.request(match(uri("/\\w*/foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/bar/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/blah/foo")), is("bar"));
            }
        });
    }

    @Test
    public void should_match_header() throws Exception {
        server.request(match(header("foo"), "bar|blah")).response(text("header"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Content barRequest = Request.Get(root()).addHeader("foo", "bar").execute().returnContent();
                assertThat(barRequest.asString(), is("header"));
                Content blahRequest = Request.Get(root()).addHeader("foo", "blah").execute().returnContent();
                assertThat(blahRequest.asString(), is("header"));
            }
        });
    }

    @Test
    public void should_eq_header() throws Exception {
        server.request(eq(header("foo"), "bar")).response("blah");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Content content = Request.Get(remoteUrl("/foo")).addHeader("foo", "bar").execute().returnContent();
                assertThat(content.asString(), is("blah"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_without_specified_header() throws Exception {
        server.request(eq(header("foo"), "bar")).response("blah");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(remoteUrl("/foo"));
            }
        });
    }

    @Test
    public void should_return_expected_response_for_specified_query() throws Exception {
        server.request(and(by(uri("/foo")), eq(query("param"), "blah"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo?param=blah")), is("bar"));
            }
        });
    }

    @Test
    public void should_match_version() throws Exception {
        server.request(by(version("HTTP/1.0"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String content = Request.Get(root()).version(HttpVersion.HTTP_1_0).execute().returnContent().asString();
                assertThat(content, is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_version() throws Exception {
        server.response(version("HTTP/1.0"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                ProtocolVersion version = Request.Get(root()).execute().returnResponse().getProtocolVersion();
                assertThat(version.getMajor(), is(1));
                assertThat(version.getMinor(), is(0));
            }
        });
    }

    @Test
    public void should_return_expected_status_code() throws Exception {
        server.response(status(200));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                int statusCode = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                assertThat(statusCode, is(200));
            }
        });
    }

    @Test
    public void should_return_expected_header() throws Exception {
        server.response(header("content-type", "application/json"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String value = Request.Get(root()).execute().returnResponse().getHeaders("content-type")[0].getValue();
                assertThat(value, is("application/json"));
            }
        });
    }

    @Test
    public void should_return_multiple_expected_header() throws Exception {
        server.response(header("content-type", "application/json"), header("foo", "bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String json = Request.Get(root()).execute().returnResponse().getHeaders("content-type")[0].getValue();
                assertThat(json, is("application/json"));
                String bar = Request.Get(root()).execute().returnResponse().getHeaders("foo")[0].getValue();
                assertThat(bar, is("bar"));
            }
        });
    }

    @Test
    public void should_not_add_content_type_header_if_exists() throws Exception {
        server.response(header("content-type", "application/xml"), new ContentHandler(text("foo")));
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
            Header[] headers = Request.Get(root()).execute().returnResponse().getHeaders("content-type");
            assertThat(headers.length, is(1));
            assertThat(headers[0].getValue(), is("application/xml"));
            }
        });
    }

    @Test
    public void should_wait_for_awhile() throws Exception {
        final long latency = 1000;
        final long delta = 200;
        server.response(latency(latency));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                long start = System.currentTimeMillis();
                helper.get(root());
                int code = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                long stop = System.currentTimeMillis();
                long gap = stop - start + delta;
                assertThat(gap, greaterThan(latency));
                assertThat(code, is(200));
            }
        });
    }

    @Test
    public void should_run_as_proxy() throws Exception {
        server.response(url("https://github.com/"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                int statusCode = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                assertThat(statusCode, is(200));
            }
        });
    }

    @Test
    public void should_return_same_http_version_without_specified_version() throws Exception {
        server.response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                ProtocolVersion version10 = Request.Get(root()).version(HttpVersion.HTTP_1_0).execute().returnResponse().getProtocolVersion();
                assertThat(version10.getMajor(), is(1));
                assertThat(version10.getMinor(), is(0));

                ProtocolVersion version11 = Request.Get(root()).version(HttpVersion.HTTP_1_1).execute().returnResponse().getProtocolVersion();
                assertThat(version11.getMajor(), is(1));
                assertThat(version11.getMinor(), is(1));
            }
        });
    }

    @Test
    public void should_return_same_http_version_without_specified_version_for_error_response() throws Exception {
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                ProtocolVersion version10 = Request.Get(root()).version(HttpVersion.HTTP_1_0).execute().returnResponse().getProtocolVersion();
                assertThat(version10.getMajor(), is(1));
                assertThat(version10.getMinor(), is(0));

                ProtocolVersion version11 = Request.Get(root()).version(HttpVersion.HTTP_1_1).execute().returnResponse().getProtocolVersion();
                assertThat(version11.getMajor(), is(1));
                assertThat(version11.getMinor(), is(1));
            }
        });
    }
}
