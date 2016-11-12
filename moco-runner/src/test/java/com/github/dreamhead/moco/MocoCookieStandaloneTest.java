package com.github.dreamhead.moco;

import com.google.common.net.HttpHeaders;
import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoCookieStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_set_and_recognize_cookie() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie");
        assertThat(decodeCookie.name(), is("login"));
        assertThat(decodeCookie.value(), is("true"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_path() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-path");
        assertThat(decodeCookie.name(), is("login"));
        assertThat(decodeCookie.value(), is("true"));
        assertThat(decodeCookie.path(), is("/"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_domain() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-domain");
        assertThat(decodeCookie.name(), is("login"));
        assertThat(decodeCookie.value(), is("true"));
        assertThat(decodeCookie.domain(), is("github.com"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_secure() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-secure");
        assertThat(decodeCookie.name(), is("login"));
        assertThat(decodeCookie.value(), is("true"));
        assertThat(decodeCookie.isSecure(), is(true));
    }

    @Test
    public void should_set_and_recognize_cookie_with_http_only() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-http-only");
        assertThat(decodeCookie.name(), is("login"));
        assertThat(decodeCookie.value(), is("true"));
        assertThat(decodeCookie.isHttpOnly(), is(true));
    }

    @Test
    public void should_set_and_recognize_cookie_with_max_age() throws IOException {
        runWithConfiguration("cookie.json");
        Cookie decodeCookie = getCookie("/cookie-with-max-age");
        assertThat(decodeCookie.name(), is("login"));
        assertThat(decodeCookie.value(), is("true"));
        assertThat(decodeCookie.maxAge(), is(3600L));
    }

    private Cookie getCookie(String uri) throws IOException {
        org.apache.http.HttpResponse response = helper.getResponse(remoteUrl(uri));

        String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
        return ClientCookieDecoder.STRICT.decode(value);
    }
}
