package com.github.dreamhead.moco;

import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoWebTest extends AbstractMocoTest {
    @Test
    public void should_set_and_recognize_cookie() throws Exception {
        server.request(eq(cookie("loggedIn"), "true")).response(status(200));
        server.response(cookie("loggedIn", "true"), status(302));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                int statusBeforeLogin = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                assertThat(statusBeforeLogin, is(302));
                int statusAfterLogin = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                assertThat(statusAfterLogin, is(200));
            }
        });
    }

    @Test
    public void should_redirect_to_expected_url() throws Exception {
        server.get(by(uri("/"))).response("foo");
        server.get(by(uri("/redirectTo"))).redirectTo(root());

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/redirectTo")), is("foo"));
            }
        });
}
}
