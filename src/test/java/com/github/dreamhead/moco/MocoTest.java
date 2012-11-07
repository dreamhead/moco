package com.github.dreamhead.moco;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.github.dreamhead.moco.Moco.*;
import static com.google.common.io.ByteStreams.toByteArray;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoTest {
    private HttpServer server;

    @Before
    public void setUp() throws Exception {
        server = httpserver(8080);
    }

    @Test
    public void should_return_expected_response() {
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(get("http://localhost:8080"), is("foo"));
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
                    assertThat(get("http://localhost:8080"), is("foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_response_from_stream() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("foo.response");

        server.response(stream(is));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(get("http://localhost:8080"), is("foo.response"));
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
                    assertThat(get("http://localhost:8080"), is("bar"));
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
                    assertThat(postContent("http://localhost:8080", "foo"), is("bar"));
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
                    assertThat(postContent("http://localhost:8080", "foo"), is("bar"));
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
                    assertThat(get("http://localhost:8080/foo"), is("bar"));
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
                    assertThat(postContent("http://localhost:8080/foo", "foo"), is("bar"));
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
                    get("http://localhost:8080/foo");
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
                    assertThat(get("http://localhost:8080/foo"), is("bar"));
                    assertThat(postContent("http://localhost:8080/foo", "foo"), is("bar"));
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
                    assertThat(get("http://localhost:8080/foo"), is("bar"));
                    assertThat(postContent("http://localhost:8080", "foo"), is("bar"));
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
                    assertThat(get("http://localhost:8080/foo"), is("bar"));
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
                    postContent("http://localhost:8080/foo", "");
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
                    assertThat(postContent("http://localhost:8080/", "foo"), is("bar"));
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
                    get("http://localhost:8080/foo");
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
                    assertThat(get("http://localhost:8080/foo"), is("bar"));
                    assertThat(get("http://localhost:8080/foo"), is("blah"));
                    assertThat(get("http://localhost:8080/foo"), is("blah"));
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
                    assertThat(get("http://localhost:8080/foo"), is("bar"));
                    assertThat(get("http://localhost:8080/foo"), is("blah"));
                    assertThat(get("http://localhost:8080/foo"), is("blah"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_content_from_specified_inputstream() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("foo.response");

        server.request(by(uri("/foo"))).response(stream(is));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(get("http://localhost:8080/foo"), is("foo.response"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_match_content_from_specified_inputstream() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("foo.request");

        server.request(by(stream(is))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(postContent("http://localhost:8080", "foo.request"), is("bar"));
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

    @Test
    public void should_return_content_based_on_xpath() {
        server.request(eq(xpath("/request/parameters/id/text()"), "1")).response("foo");
        server.request(eq(xpath("/request/parameters/id/text()"), "2")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertThat(postFile("http://localhost:8080", "foo.xml"), is("foo"));
                    assertThat(postFile("http://localhost:8080", "bar.xml"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private String get(String uri) throws IOException {
        Content content = Request.Get(uri).execute().returnContent();
        return content.asString();
    }

    private String postContent(String uri, String postContent) throws IOException {
        Content content = Request.Post(uri).bodyByteArray(postContent.getBytes())
                .execute().returnContent();
        return content.asString();
    }

    private String postFile(String uri, String file) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        Content content = Request.Post(uri).bodyByteArray(toByteArray(is))
                .execute().returnContent();
        return content.asString();
    }
}
