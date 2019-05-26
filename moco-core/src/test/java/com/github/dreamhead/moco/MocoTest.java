package com.github.dreamhead.moco;

import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.HttpProtocolVersion.VERSION_1_0;
import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.asHeader;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.contain;
import static com.github.dreamhead.moco.Moco.cycle;
import static com.github.dreamhead.moco.Moco.endsWith;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.exist;
import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.latency;
import static com.github.dreamhead.moco.Moco.match;
import static com.github.dreamhead.moco.Moco.method;
import static com.github.dreamhead.moco.Moco.not;
import static com.github.dreamhead.moco.Moco.or;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.seq;
import static com.github.dreamhead.moco.Moco.startsWith;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Moco.version;
import static com.github.dreamhead.moco.Moco.with;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.google.common.collect.ImmutableMultimap.of;
import static com.google.common.io.Files.toByteArray;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class MocoTest extends AbstractMocoHttpTest {
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
    public void should_return_expected_response_from_file_with_charset() throws Exception {
        server.response(file("src/test/resources/gbk.response", Charset.forName("GBK")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getAsBytes(root()), is(toByteArray(new File("src/test/resources/gbk.response"))));
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
    public void should_return_expected_response_from_path_resource_with_charset() throws Exception {
        server.response(pathResource("gbk.response", Charset.forName("GBK")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getAsBytes(root()), is(toByteArray(new File("src/test/resources/gbk.response"))));
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_path_resource() throws Exception {
        server.request(by(pathResource("foo.request"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                URL resource = Resources.getResource("foo.request");
                InputStream stream = resource.openStream();
                assertThat(helper.postStream(root(), stream), is("foo"));
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
    public void should_match_request_with_charset_from_file() throws Exception {
        final Charset gbk = Charset.forName("GBK");
        server.request(by(file("src/test/resources/gbk.response", gbk))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postBytes(root(), toByteArray(new File("src/test/resources/gbk.response")), gbk),
                        is("bar"));
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
        server.request(and(by("foo"), by(uri("/foo")))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.get(remoteUrl("/foo"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_either_matcher() throws Exception {
        server.request(or(by("foo"), by(uri("/foo")))).response("bar");

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
        server.request(not(by(uri("/foo")))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/bar")), is("bar"));
            }
        });
    }

    @Test
    public void should_match_request_based_on_simplified_either_matcher() throws Exception {
        server.request(by("foo"), by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.postContent(root(), "foo"), is("bar"));
            }
        });
    }

    @Test
    public void should_match_get_method_by_method_api_with_http_method() throws Exception {
        server.request(and(by(uri("/foo")), by(method(HttpMethod.GET)))).response("bar");

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
        server.get(by(uri("/foo"))).response("bar");

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
                Request request = Request.Put(remoteUrl("/foo"));
                assertThat(helper.executeAsString(request), is("bar"));
            }
        });
    }

    @Test
    public void should_match_delete_method_via_api() throws Exception {
        server.request(and(by(uri("/foo")), by(method("delete")))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Delete(remoteUrl("/foo"));
                String response = helper.executeAsString(request);
                assertThat(response, is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_response_for_post_while_http_method_is_not_post() throws Exception {
        server.post(by(uri("/foo"))).response("bar");

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
    public void should_return_content_circularly_one_by_one() throws Exception {
        server.request(by(uri("/foo"))).response(cycle("bar", "blah"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("blah"));
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("blah"));
            }
        });
    }

    @Test
    public void should_return_content_one_by_one_with_text_api_circularly() throws Exception {
        server.request(by(uri("/foo"))).response(cycle(text("bar"), text("blah")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("blah"));
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("blah"));
            }
        });
    }

    @Test
    public void should_return_response_circularly_one_by_one() throws Exception {
        server.request(by(uri("/foo"))).response(cycle(status(302), status(200)));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(302));
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(200));
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(302));
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(200));
            }
        });
    }

    @Test
    public void should_match() throws Exception {
        server.request(match(uri("/\\w*/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/bar/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/blah/foo")), is("bar"));
            }
        });
    }

    @Test
    public void should_match_method() throws Exception {
        server.request(match(method("get|post"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/bar/foo")), is("bar"));
                assertThat(helper.postContent(remoteUrl("/blah/foo"), "content"), is("bar"));
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
        server.request(exist(header("foo"))).response("header");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getWithHeader(root(), of("foo", "bar")), is("header"));
                assertThat(helper.getWithHeader(root(), of("foo", "blah")), is("header"));
            }
        });
    }

    @Test
    public void should_exist_query() throws Exception {
        server.request(exist(query("foo"))).response("query");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/path?foo")), is("query"));
                assertThat(helper.get(remoteUrl("/other?foo")), is("query"));
            }
        });
    }

    @Test
    public void should_starts_with() throws Exception {
        server.request(startsWith(uri("/foo"))).response("bar");

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
        server.request(startsWith(header("foo"), "bar")).response("bar");

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
        server.request(endsWith(uri("foo"))).response("bar");

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
        server.request(endsWith(header("foo"), "bar")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getWithHeader(root(), of("foo", "Abar")), is("bar"));
                assertThat(helper.getWithHeader(root(), of("foo", "Bbar")), is("bar"));
            }
        });
    }

    @Test
    public void should_contain() throws Exception {
        server.request(contain(uri("foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/a/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo/a")), is("bar"));
            }
        });
    }

    @Test
    public void should_contain_for_resource() throws Exception {
        server.request(contain(header("foo"), "bar")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.getWithHeader(root(), of("foo", "Abar")), is("bar"));
                assertThat(helper.getWithHeader(root(), of("foo", "barA")), is("bar"));
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

    @Test
    public void should_eq_multiple_header_with_same_name() throws Exception {
        server.request(and(eq(header("foo"), "bar")), eq(header("foo"), "bar2"))
                .response("blah");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getWithHeader(root(), of("foo", "bar", "foo", "bar2")), is("blah"));
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
    public void should_set_multiple_header_with_same_name() throws Exception {
        server.response(header("foo", "bar"), header("foo", "another"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(root());
                Header[] headers = response.getHeaders("foo");
                assertThat(headers.length, is(2));
                assertThat(headers[0].getValue(), is("bar"));
                assertThat(headers[1].getValue(), is("another"));
            }
        });
    }

    @Test
    public void should_return_expected_response_for_multiple_specified_query() throws Exception {
        server.request(and(by(uri("/foo")), eq(query("param"), "blah"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo?param=multiple&param=blah")), is("bar"));
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
    public void should_return_expected_response_for_empty_query() throws Exception {
        server.request(and(by(uri("/foo")), eq(query("param"), ""))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/foo?param")), is("bar"));
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
                ProtocolVersion version = helper.getResponse(root()).getProtocolVersion();
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
                ProtocolVersion version = helper.getResponse(root()).getProtocolVersion();
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
    public void should_return_response_with_expected_header() throws Exception {
        server.response(with(asHeader(HttpHeaders.CONTENT_TYPE, text("application/json"))));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String value = helper.getResponse(root()).getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                assertThat(value, is("application/json"));
            }
        });
    }

    @Test
    public void should_return_expected_header() throws Exception {
        server.response(header(HttpHeaders.CONTENT_TYPE, "application/json"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String value = helper.getResponse(root()).getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                assertThat(value, is("application/json"));
            }
        });
    }

    @Test
    public void should_return_multiple_expected_header() throws Exception {
        server.response(header(HttpHeaders.CONTENT_TYPE, "application/json"), header("foo", "bar"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String json = helper.getResponse(root()).getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                assertThat(json, is("application/json"));
                String bar = helper.getResponse(root()).getFirstHeader("foo").getValue();
                assertThat(bar, is("bar"));
            }
        });
    }

    @Test
    public void should_return_multiple_expected_header_with_same_name() throws Exception {
        server.response(header("foo", "bar"), header("foo", "moco"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Header[] headers = helper.getResponse(root()).getHeaders("foo");
                assertThat(headers.length, is(2));
                assertThat(headers[0].getValue(), is("bar"));
                assertThat(headers[1].getValue(), is("moco"));
            }
        });
    }

    @Test
    public void should_wait_for_awhile() throws Exception {
        final long latency = 1000;
        final long delta = 200;
        server.response(latency(latency, TimeUnit.MILLISECONDS));

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
    public void should_wait_for_awhile_with_time_unit() throws Exception {
        final long delta = 200;
        server.response(latency(1, TimeUnit.SECONDS));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                long start = System.currentTimeMillis();
                helper.get(root());
                int code = helper.getForStatus(root());
                long stop = System.currentTimeMillis();
                long gap = stop - start + delta;
                assertThat(gap, greaterThan(TimeUnit.SECONDS.toMillis(1)));
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
                ProtocolVersion version10 = helper.execute(Request.Get(root())
                        .version(HttpVersion.HTTP_1_0))
                        .getProtocolVersion();
                assertThat(version10.getMajor(), is(1));
                assertThat(version10.getMinor(), is(0));

                ProtocolVersion version11 = helper.execute(Request.Get(root())
                        .version(HttpVersion.HTTP_1_1))
                        .getProtocolVersion();
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
                ProtocolVersion version10 = helper.execute(Request.Get(root())
                        .version(HttpVersion.HTTP_1_0))
                        .getProtocolVersion();
                assertThat(version10.getMajor(), is(1));
                assertThat(version10.getMinor(), is(0));

                ProtocolVersion version11 = helper.execute(Request.Get(root())
                        .version(HttpVersion.HTTP_1_1))
                        .getProtocolVersion();
                assertThat(version11.getMajor(), is(1));
                assertThat(version11.getMinor(), is(1));
            }
        });
    }

    @Test
    public void should_return_default_content_type() throws Exception {
        server.response(with("foo"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = helper.getResponse(root()).getFirstHeader(HttpHeaders.CONTENT_TYPE);
                assertThat(header.getValue(), is("text/plain; charset=utf-8"));
            }
        });
    }

    @Test
    public void should_return_specified_content_type() throws Exception {
        server.response(with("foo"), header(HttpHeaders.CONTENT_TYPE, "text/html"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = helper.getResponse(root()).getFirstHeader(HttpHeaders.CONTENT_TYPE);
                assertThat(header.getValue(), is("text/html"));
            }
        });
    }

    @Test
    public void should_return_specified_content_type_no_matter_order() throws Exception {
        server.response(header(HttpHeaders.CONTENT_TYPE, "text/html"), with("foo"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = helper.getResponse(root()).getFirstHeader(HttpHeaders.CONTENT_TYPE);
                assertThat(header.getValue(), is("text/html"));
            }
        });
    }

    @Test
    public void should_return_specified_content_type_with_case_insensitive() throws Exception {
        server.response(header("content-type", "text/html"), with("foo"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header[] headers = helper.getResponse(root()).getHeaders(HttpHeaders.CONTENT_TYPE);
                assertThat(headers.length, is(1));
            }
        });
    }

    @Test
    public void should_return_response_with_and_handler() throws Exception {
        server.request(by(uri("/foo"))).response(seq(
                and(with(text("foo")), status(302)),
                and(with(text("bar")), status(302)),
                and(with(text("run")), status(200)))
        );

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(302));
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(302));
                assertThat(helper.getForStatus(remoteUrl("/foo")), is(200));
            }
        });
    }
}
