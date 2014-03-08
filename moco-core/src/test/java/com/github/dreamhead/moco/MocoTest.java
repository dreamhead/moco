package com.github.dreamhead.moco;

import com.google.common.io.ByteStreams;
import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.github.dreamhead.moco.HttpProtocolVersion.VERSION_1_0;
import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static com.google.common.collect.ImmutableMap.of;
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
    public void should_return_expected_response_with_content_api() throws Exception {
        server.response(with("foo"));

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
    public void should_return_expected_response_from_path_resource() throws Exception {
        server.response(pathResource("foo.response"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_path_resource() throws Exception {
        server.request(by(pathResource("foo.request"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                InputStream asStream = this.getClass().getClassLoader().getResourceAsStream("foo.request");
                assertThat(helper.postBytes(root(), ByteStreams.toByteArray(asStream)), is("foo"));
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
    public void should_match_request_based_on_not_matcher() throws Exception {
        server.request(not(by(uri("/foo")))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/bar")), is("bar"));
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
    public void should_return_response_one_by_one() throws Exception {
        server.request(by(uri("/foo"))).response(seq(status(302), status(302), status(200)));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(302));
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(302));
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(200));
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
        server.request(match(header("foo"), "bar|blah")).response("header");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getWithHeader(root(), of("foo", "bar")), is("header"));
                assertThat(helper.getWithHeader(root(), of("foo", "blah")), is("header"));
            }
        });
    }

    @Test
    public void should_exist_header() throws Exception {
        server.request(exist(header("foo"))).response(text("header"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getWithHeader(root(), of("foo", "bar")), is("header"));
                assertThat(helper.getWithHeader(root(), of("foo", "blah")), is("header"));
            }
        });
    }

    @Test
    public void should_starts_with() throws Exception {
        server.request(startsWith(uri("/foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo/a")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo/b")), is("bar"));
            }
        });
    }

    @Test
    public void should_starts_with_for_resource() throws Exception {
        server.request(startsWith(header("foo"), "bar")).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getWithHeader(root(), of("foo", "barA")), is("bar"));
                assertThat(helper.getWithHeader(root(), of("foo", "barB")), is("bar"));
            }
        });
    }

    @Test
    public void should_ends_with() throws Exception {
        server.request(endsWith(uri("foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/a/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/b/foo")), is("bar"));
            }
        });
    }

    @Test
    public void should_ends_with_for_resource() throws Exception {
        server.request(endsWith(header("foo"), "bar")).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getWithHeader(root(), of("foo", "Abar")), is("bar"));
                assertThat(helper.getWithHeader(root(), of("foo", "Bbar")), is("bar"));
            }
        });
    }

    @Test
    public void should_eq_header() throws Exception {
        server.request(eq(header("foo"), "bar")).response("blah");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getWithHeader(root(), of("foo", "bar")), is("blah"));
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
        server.request(by(version(VERSION_1_0))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getWithVersion(root(), HttpVersion.HTTP_1_0), is("foo"));
            }
        });
    }

    @Test
    public void should_return_expected_version() throws Exception {
        server.response(version(VERSION_1_0));

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
    public void should_return_excepted_version_with_version_api() throws Exception {
        server.response(version(VERSION_1_0));

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
                assertThat(helper.getForStatus(root()), is(200));
            }
        });
    }

    @Test
    public void should_return_expected_header() throws Exception {
        server.response(header("content-type", "application/json"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String value = Request.Get(root()).execute().returnResponse().getFirstHeader("content-type").getValue();
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
                String json = Request.Get(root()).execute().returnResponse().getFirstHeader("content-type").getValue();
                assertThat(json, is("application/json"));
                String bar = Request.Get(root()).execute().returnResponse().getFirstHeader("foo").getValue();
                assertThat(bar, is("bar"));
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
                int code = helper.getForStatus(root());
                long stop = System.currentTimeMillis();
                long gap = stop - start + delta;
                assertThat(gap, greaterThan(latency));
                assertThat(code, is(200));
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

    @Test
    public void should_match_form_value() throws Exception {
        server.post(eq(form("name"), "dreamhead")).response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String content = Request.Post(root()).bodyForm(new BasicNameValuePair("name", "dreamhead")).execute().returnContent().asString();
                assertThat(content, is("foobar"));
            }
        });
    }

    @Test
    public void should_no_exception_form_get_request() throws Exception {
        server.request(eq(form("password"), "hello")).response("foobar");
        server.response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(root()), is("foobar"));
            }
        });
    }

    @Test
    public void should_return_default_content_type() throws Exception {
        server.response(with(text("foo")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = Request.Get(root()).execute().returnResponse().getFirstHeader("Content-Type");
                assertThat(header.getValue(), is("text/plain; charset=UTF-8"));
            }
        });
    }

    @Test
    public void should_return_specified_content_type() throws Exception {
        server.response(with(text("foo")), header("Content-Type", "text/html"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = Request.Get(root()).execute().returnResponse().getFirstHeader("Content-Type");
                assertThat(header.getValue(), is("text/html"));
            }
        });
    }

    @Test
    public void should_return_specified_content_type_no_matter_order() throws Exception {
        server.response(header("Content-Type", "text/html"), with(text("foo")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = Request.Get(root()).execute().returnResponse().getFirstHeader("Content-Type");
                assertThat(header.getValue(), is("text/html"));
            }
        });
    }
}
