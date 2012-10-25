package com.github.moco;

import com.github.moco.internal.MocoHttpServer;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
                    Content content = Request.Get("http://localhost:8080")
                            .execute().returnContent();
                    assertThat(content.asString(), is("foo"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Test
    public void should_return_expected_response_based_on_specified_request() {
        server.request(eqContent("foo")).response("bar");

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
        server.request(eqUri("/foo")).response("bar");

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
                    Content content = Request.Get("http://localhost:8080")
                            .execute().returnContent();
                    assertThat(content.asString(), is("bar"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
