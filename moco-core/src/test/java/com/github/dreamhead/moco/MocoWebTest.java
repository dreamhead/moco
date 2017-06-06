package com.github.dreamhead.moco;

import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.CookieAttribute.domain;
import static com.github.dreamhead.moco.CookieAttribute.httpOnly;
import static com.github.dreamhead.moco.CookieAttribute.maxAge;
import static com.github.dreamhead.moco.CookieAttribute.path;
import static com.github.dreamhead.moco.CookieAttribute.secure;
import static com.github.dreamhead.moco.Moco.attachment;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.cookie;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.form;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.Moco.status;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.google.common.collect.ImmutableSet.of;
import static org.apache.http.client.fluent.Request.Post;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoWebTest extends AbstractMocoHttpTest {
    @Test
    public void should_match_form_value() throws Exception {
        server.post(eq(form("name"), "dreamhead")).response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                org.apache.http.client.fluent.Request request = Post(root()).bodyForm(new BasicNameValuePair("name", "dreamhead"));
                assertThat(helper.executeAsString(request), is("foobar"));
            }
        });
    }

    @Test
    public void should_match_form_value_with_charset() throws Exception {
        server = httpServer(port(), log());
        server.post(eq(form("name"), "表单")).response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                org.apache.http.client.fluent.Request request = Post(root()).bodyForm(of(new BasicNameValuePair("name", "表单")), Charset.forName("GBK"));
                assertThat(helper.executeAsString(request), is("foobar"));
            }
        });
    }

    @Test
    public void should_no_exception_form_get_request() throws Exception {
        server.request(eq(form("password"), "hello")).response("foobar");
        server.response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(root()), is("foobar"));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie() throws Exception {
        server.request(eq(cookie("loggedIn"), "true")).response(status(200));
        server.response(cookie("loggedIn", "true"), status(302));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.getForStatus(root()), is(302));
                assertThat(helper.getForStatus(root()), is(200));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_path() throws Exception {
        server.request(eq(cookie("loggedIn"), "true")).response(status(200));
        server.response(cookie("loggedIn", "true", path("/")), status(302));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                org.apache.http.HttpResponse response = helper.getResponse(root());
                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = ClientCookieDecoder.STRICT.decode(value);
                assertThat(decodeCookie.path(), is("/"));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_max_age() throws Exception {
        server.request(eq(cookie("loggedIn"), "true")).response(status(200));
        server.response(cookie("loggedIn", "true", maxAge(1, TimeUnit.HOURS)), status(302));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                org.apache.http.HttpResponse response = helper.getResponse(root());

                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = ClientCookieDecoder.STRICT.decode(value);
                assertThat(decodeCookie.maxAge(), is(3600L));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_secure() throws Exception {
        server.request(eq(cookie("loggedIn"), "true")).response(status(200));
        server.response(cookie("loggedIn", "true", secure()), status(302));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                org.apache.http.HttpResponse response = helper.getResponse(root());
                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = ClientCookieDecoder.STRICT.decode(value);
                assertThat(decodeCookie.isSecure(), is(true));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_httponly() throws Exception {
        server.request(eq(cookie("loggedIn"), "true")).response(status(200));
        server.response(cookie("loggedIn", "true", httpOnly()), status(302));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                org.apache.http.HttpResponse response = helper.getResponse(root());
                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = ClientCookieDecoder.STRICT.decode(value);
                assertThat(decodeCookie.isHttpOnly(), is(true));
            }
        });
    }

    @Test
    public void should_set_and_recognize_cookie_with_domain() throws Exception {
        server.request(eq(cookie("loggedIn"), "true")).response(status(200));
        server.response(cookie("loggedIn", "true", domain("localhost")), status(302));
        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                org.apache.http.HttpResponse response = helper.getResponse(root());
                String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
                Cookie decodeCookie = ClientCookieDecoder.STRICT.decode(value);
                assertThat(decodeCookie.domain(), is("localhost"));
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

    @Test
    public void should_redirect_for_any_response() throws Exception {
        server.get(by(uri("/"))).response("foo");
        server.redirectTo(root());

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(remoteUrl("/redirectTo")), is("foo"));
            }
        });
    }

    @Test
    public void should_download_attachment() throws Exception {
        server.get(by(uri("/"))).response(attachment("foo.txt", file("src/test/resources/foo.response")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/")), is("foo.response"));
            }
        });
    }
}
