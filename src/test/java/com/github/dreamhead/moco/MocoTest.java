package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.google.common.io.Resources;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.github.dreamhead.moco.Moco.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoTest {
    private HttpServer server;
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
        server = httpserver(8080);
    }

    @Test
    public void should_return_expected_response() {
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get("http://localhost:8080"), is("foo"));
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
                    assertThat(helper.get("http://localhost:8080"), is("foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_response_from_stream() throws IOException {
        InputStream is = Resources.getResource("foo.response").openStream();

        server.response(stream(is));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get("http://localhost:8080"), is("foo.response"));
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
                    assertThat(helper.get("http://localhost:8080"), is("foo.response"));
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
                    assertThat(helper.get("http://localhost:8080"), is("bar"));
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
                    assertThat(helper.postContent("http://localhost:8080", "foo"), is("bar"));
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
                    assertThat(helper.postContent("http://localhost:8080", "foo"), is("bar"));
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
                    assertThat(helper.get("http://localhost:8080/foo"), is("bar"));
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
                    assertThat(helper.postContent("http://localhost:8080/foo", "foo"), is("bar"));
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
                    helper.get("http://localhost:8080/foo");
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
                    assertThat(helper.get("http://localhost:8080/foo"), is("bar"));
                    assertThat(helper.postContent("http://localhost:8080/foo", "foo"), is("bar"));
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
                    assertThat(helper.get("http://localhost:8080/foo"), is("bar"));
                    assertThat(helper.postContent("http://localhost:8080", "foo"), is("bar"));
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
                    assertThat(helper.get("http://localhost:8080/foo"), is("bar"));
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
                    assertThat(helper.get("http://localhost:8080/foo"), is("bar"));
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
                    helper.postContent("http://localhost:8080/foo", "");
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
                    assertThat(helper.postContent("http://localhost:8080/", "foo"), is("bar"));
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
                    helper.get("http://localhost:8080/foo");
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
                    assertThat(helper.get("http://localhost:8080/foo"), is("bar"));
                    assertThat(helper.get("http://localhost:8080/foo"), is("blah"));
                    assertThat(helper.get("http://localhost:8080/foo"), is("blah"));
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
                    assertThat(helper.get("http://localhost:8080/foo"), is("bar"));
                    assertThat(helper.get("http://localhost:8080/foo"), is("blah"));
                    assertThat(helper.get("http://localhost:8080/foo"), is("blah"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_content_from_specified_inputstream() throws IOException {
        InputStream is = Resources.getResource("foo.response").openStream();

        server.request(by(uri("/foo"))).response(stream(is));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.get("http://localhost:8080/foo"), is("foo.response"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_match_content_from_specified_inputstream() throws IOException {
        InputStream is = Resources.getResource("foo.request").openStream();

        server.request(by(stream(is))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(helper.postContent("http://localhost:8080", "foo.request"), is("bar"));
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
                    Content content = Request.Get("http://localhost:8080/foo").addHeader("foo", "bar").execute().returnContent();
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
                    helper.get("http://localhost:8080/foo");
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
                    assertThat(helper.get("http://localhost:8080/foo?param=blah"), is("bar"));
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
                    assertThat(helper.postFile("http://localhost:8080", "foo.xml"), is("foo"));
                    assertThat(helper.postFile("http://localhost:8080", "bar.xml"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_status_code() {
        server.response(status(200));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    int statusCode = Request.Get("http://localhost:8080").execute().returnResponse().getStatusLine().getStatusCode();
                    assertThat(statusCode, is(200));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
