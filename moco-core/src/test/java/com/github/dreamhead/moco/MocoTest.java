package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoTest {
    private HttpServer server;
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
        server = httpserver(port());
    }

    @Test
    public void should_return_expected_response() {
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(root()), is("foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_response_with_text_api() {
        server.response(text("foo"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(root()), is("foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_response_from_file() throws IOException {
        server.response(file("src/test/resources/foo.response"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(root()), is("foo.response"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_for_unknown_request() {
        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(root()), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_request() {
        server.request(by("foo")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.postContent(root(), "foo"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_request_with_text_api() {
        server.request(by(text("foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.postContent(root(), "foo"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_uri() {
        server.request(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_match_request_based_on_multiple_matchers() {
        server.request(and(by("foo"), by(uri("/foo")))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.postContent(remoteUrl("/foo"), "foo"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_even_if_match_one_of_conditions() {
        server.request(and(by("foo"), by(uri("/foo")))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    helper.get(remoteUrl("/foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_match_request_based_on_either_matcher() {
        server.request(or(by("foo"), by(uri("/foo")))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                    assertThat(helper.postContent(remoteUrl("/foo"), "foo"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_match_request_based_on_simplified_either_matcher() {
        server.request(by("foo"), by(uri("/foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                    assertThat(helper.postContent(root(), "foo"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_match_get_method() {
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_match_get_method_by_method_api() {
        server.request(and(by(uri("/foo")), by(method("get")))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void should_not_response_for_get_while_http_method_is_not_get() {
        server.get(by(uri("/foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    helper.postContent(remoteUrl("/foo"), "");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_match_post_method() {
        server.post(by("foo")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.postContent(root(), "foo"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void should_not_response_for_post_while_http_method_is_not_post() {
        server.post(by(uri("/foo"))).response(text("bar"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    helper.get(remoteUrl("/foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_content_one_by_one() {
        server.request(by(uri("/foo"))).response(seq("bar", "blah"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                    assertThat(helper.get(remoteUrl("/foo")), is("blah"));
                    assertThat(helper.get(remoteUrl("/foo")), is("blah"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_content_one_by_one_with_text_api() {
        server.request(by(uri("/foo"))).response(seq(text("bar"), text("blah")));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                    assertThat(helper.get(remoteUrl("/foo")), is("blah"));
                    assertThat(helper.get(remoteUrl("/foo")), is("blah"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_match_header() {
        server.request(eq(header("foo"), "bar")).response("blah");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    Content content = Request.Get(remoteUrl("/foo")).addHeader("foo", "bar").execute().returnContent();
                    assertThat(content.asString(), is("blah"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_without_specified_header() {
        server.request(eq(header("foo"), "bar")).response("blah");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    helper.get(remoteUrl("/foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_response_for_specified_query() {
        server.request(and(by(uri("/foo")), eq(query("param"), "blah"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get(remoteUrl("/foo?param=blah")), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_content_based_on_xpath() {
        server.request(eq(xpath("/request/parameters/id/text()"), "1")).response("foo");
        server.request(eq(xpath("/request/parameters/id/text()"), "2")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.postFile(root(), "foo.xml"), is("foo"));
                    assertThat(helper.postFile(root(), "bar.xml"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_status_code() {
        server.response(status(200));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    int statusCode = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                    assertThat(statusCode, is(200));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_header() {
        server.response(header("content-type", "application/json"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    String value = Request.Get(root()).execute().returnResponse().getHeaders("content-type")[0].getValue();
                    assertThat(value, is("application/json"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_multiple_expected_header() {
        server.response(header("content-type", "application/json"), header("foo", "bar"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    String json = Request.Get(root()).execute().returnResponse().getHeaders("content-type")[0].getValue();
                    assertThat(json, is("application/json"));
                    String bar = Request.Get(root()).execute().returnResponse().getHeaders("foo")[0].getValue();
                    assertThat(bar, is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_run_as_proxy() throws IOException {
        server.response(url("http://www.github.com"));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    int statusCode = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                    assertThat(statusCode, is(200));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
