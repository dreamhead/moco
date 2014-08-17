package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.AbstractMocoHttpTest;
import com.github.dreamhead.moco.Runnable;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.Test;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MocoConnectionTest extends AbstractMocoHttpTest {
    @Test
    public void should_keep_alive_for_1_0_keep_alive_request() throws Exception {
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Response response = Request.Get(root()).version(HttpVersion.HTTP_1_0).addHeader("Connection", "keep-alive").execute();
                String connection = response.returnResponse().getFirstHeader("Connection").getValue();
                assertThat(connection, is("keep-alive"));
            }
        });
    }

    @Test
    public void should_not_have_keep_alive_header_for_1_1_keep_alive_request() throws Exception {
        server.response("foo");

        running(server, new com.github.dreamhead.moco.Runnable() {
            @Override
            public void run() throws Exception {
                Response response = Request.Get(root()).version(HttpVersion.HTTP_1_1).addHeader("Connection", "keep-alive").execute();
                assertThat(response.returnResponse().getFirstHeader("Connection"), nullValue());
            }
        });
    }

    @Test
    public void should_not_keep_alive_for_close_request() throws Exception {
        server.response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Response response = Request.Get(root()).addHeader("Connection", "close").execute();
                assertThat(response.returnResponse().getFirstHeader("Connection"), nullValue());
            }
        });
    }
}
