package com.github.moco;

import com.github.moco.internal.MocoHttpServer;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.github.moco.Moco.*;
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
    public void should_return_expected_response_based_on_specified_request() {
        server.request(eq(text("foo"))).response(text("bar"));

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
        server.request(eq(uri("/foo"))).response("bar");

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
        server.request(eq(uri("/foo"))).response(seq("bar", "blah"));

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

        server.request(eq(uri("/foo"))).response(stream(is));

        running(server, new Runnable() {
            @Override
            public void run() {
                try {
                    assertContentFromUri("http://localhost:8080/foo", "foo");
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
        Content content = Request.Get(uri)
                .execute().returnContent();
        return content.asString();
    }
}
