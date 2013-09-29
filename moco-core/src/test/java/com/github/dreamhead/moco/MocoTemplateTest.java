package com.github.dreamhead.moco;

import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoTemplateTest extends AbstractMocoTest {
    @Test
    public void should_generate_response_with_http_method() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.method}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/template")), is("GET"));
                assertThat(helper.postContent(remoteUrl("/template"), "foo"), is("POST"));
            }
        });
    }

    @Test
    public void should_generate_response_with_http_version() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.version}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = Request.Get(remoteUrl("/template")).version(HttpVersion.HTTP_1_0).execute().returnContent().asString();
                assertThat(response, is("HTTP/1.0"));
            }
        });
    }

    @Test
    public void should_generate_response_with_content() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.content}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(remoteUrl("/template"), "foo"), is("foo"));
            }
        });
    }

    @Test
    public void should_generate_response_with_http_header() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.headers['foo']}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = Request.Get(remoteUrl("/template")).addHeader("foo", "bar").execute().returnContent().asString();
                assertThat(response, is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_for_unknown_header() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.headers['foo']}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(remoteUrl("/template"));
            }
        });
    }

    @Test
    public void should_generate_response_with_http_query() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.queries['foo']}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = helper.get(remoteUrl("/template?foo=bar"));
                assertThat(response, is("bar"));
            }
        });
    }

    @Test
    public void should_generate_response_from_file() throws Exception {
        server.request(by(uri("/template"))).response(template(file("src/test/resources/foo.template")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/template")), is("GET"));
            }
        });
    }

    @Test
    public void should_generate_response_version() throws Exception {
        server.request(by(uri("/template"))).response(version(template("${req.version}")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                ProtocolVersion version = Request.Get(remoteUrl("/template")).version(HttpVersion.HTTP_1_0).execute().returnResponse().getProtocolVersion();
                assertThat(version.toString(), is("HTTP/1.0"));
            }
        });
    }

    @Test
    public void should_generate_response_header() throws Exception {
        server.request(by(uri("/template"))).response(header("foo", template("${req.method}")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = Request.Get(remoteUrl("/template")).version(HttpVersion.HTTP_1_0).execute().returnResponse().getFirstHeader("foo");
                assertThat(header.getValue(), is("GET"));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie() throws Exception {
        server.request(eq(cookie("loggedIn"), "GET")).response(status(200));
        server.response(cookie("loggedIn", template("${req.method}")), status(302));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getForStatus(root()), is(302));
                assertThat(helper.getForStatus(root()), is(200));
            }
        });
    }

    @Test
    public void should_generate_uri() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.uri}"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String response = helper.get(remoteUrl("/template"));
                assertThat(response, is("/template"));
            }
        });
    }
}
