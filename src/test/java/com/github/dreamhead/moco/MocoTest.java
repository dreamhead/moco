package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.MocoHttpServer;
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
    private MocoHttpServer server;

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
                    assertContentFromUri("http://localhost:8080", "foo");
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
                    assertContentFromUri("http://localhost:8080", "foo");
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
                    assertContentFromUri("http://localhost:8080", "foo.response");
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
                    Content content = Request.Post("http://localhost:8080").bodyByteArray("foo".getBytes())
                            .execute().returnContent();
                    assertThat(content.asString(), is("bar"));
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
                    Content content = Request.Post("http://localhost:8080").bodyByteArray("foo".getBytes())
                            .execute().returnContent();
                    assertThat(content.asString(), is("bar"));
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
                    Content content = Request.Get("http://localhost:8080/foo")
                            .execute().returnContent();
                    assertThat(content.asString(), is("bar"));
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
                    assertContentFromUri("http://localhost:8080", "bar");
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
                    assertContentFromUri("http://localhost:8080/foo", "bar");
                    assertContentFromUri("http://localhost:8080/foo", "blah");
                    assertContentFromUri("http://localhost:8080/foo", "blah");
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
                    assertContentFromUri("http://localhost:8080/foo", "bar");
                    assertContentFromUri("http://localhost:8080/foo", "blah");
                    assertContentFromUri("http://localhost:8080/foo", "blah");
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
                    assertContentFromUri("http://localhost:8080/foo", "foo.response");
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
                    Content content = Request.Post("http://localhost:8080").bodyByteArray("foo.request".getBytes())
                            .execute().returnContent();
                    assertThat(content.asString(), is("bar"));
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
                    assertThat(post("foo.xml", "http://localhost:8080"), is("foo"));
                    assertThat(post("bar.xml", "http://localhost:8080"), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void assertContentFromUri(String uri, String expectedContent) throws IOException {
        assertThat(get(uri), is(expectedContent));
    }

    private String get(String uri) throws IOException {
        Content content = Request.Get(uri).execute().returnContent();
        return content.asString();
    }

    private String post(String file, String uri) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        Content content = Request.Post(uri).bodyByteArray(toByteArray(is))
                .execute().returnContent();
        return content.asString();
    }
}
