package com.github.dreamhead.moco;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpCookie;
import com.github.dreamhead.moco.util.HttpHeaders;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MocoCookieStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_set_and_recognize_cookie() throws IOException {
        runWithConfiguration("cookie.json");
        HttpCookie decodeCookie = getCookie("/cookie");
        assertThat(decodeCookie.getName(), is("login"));
        assertThat(decodeCookie.getValue(), is("true"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_path() throws IOException {
        runWithConfiguration("cookie.json");
        HttpCookie decodeCookie = getCookie("/cookie-with-path");
        assertThat(decodeCookie.getName(), is("login"));
        assertThat(decodeCookie.getValue(), is("true"));
        assertThat(decodeCookie.getPath(), is("/"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_domain() throws IOException {
        runWithConfiguration("cookie.json");
        HttpCookie decodeCookie = getCookie("/cookie-with-domain");
        assertThat(decodeCookie.getName(), is("login"));
        assertThat(decodeCookie.getValue(), is("true"));
        assertThat(decodeCookie.getDomain(), is("github.com"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_secure() throws IOException {
        runWithConfiguration("cookie.json");
        HttpCookie decodeCookie = getCookie("/cookie-with-secure");
        assertThat(decodeCookie.getName(), is("login"));
        assertThat(decodeCookie.getValue(), is("true"));
        assertThat(decodeCookie.getSecure(), is(true));
    }

    @Test
    public void should_set_and_recognize_cookie_with_http_only() throws IOException {
        runWithConfiguration("cookie.json");
        HttpCookie decodeCookie = getCookie("/cookie-with-http-only");
        assertThat(decodeCookie.getName(), is("login"));
        assertThat(decodeCookie.getValue(), is("true"));
        assertThat(decodeCookie.isHttpOnly(), is(true));
    }

    @Test
    public void should_set_and_recognize_cookie_with_max_age() throws IOException {
        runWithConfiguration("cookie.json");
        HttpCookie decodeCookie = getCookie("/cookie-with-max-age");
        assertThat(decodeCookie.getName(), is("login"));
        assertThat(decodeCookie.getValue(), is("true"));
        assertThat(decodeCookie.getMaxAge(), is(3600L));
    }

    @Test
    public void should_set_and_recognize_cookie_with_same_site() throws IOException {
        runWithConfiguration("cookie.json");
        String value = getCookieHeader("/cookie-with-same-site");
        assertThat(value.contains("login=true"), is(true));
        assertThat(value.contains("SameSite=Lax"), is(true));
    }

    private HttpCookie getCookie(String uri) throws IOException {
        ClassicHttpResponse response = helper.getResponse(remoteUrl(uri));
        String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
        return HttpCookie.parse(value).get(0);
    }

    private String getCookieHeader(String uri) throws IOException {
        ClassicHttpResponse response = helper.getResponse(remoteUrl(uri));
        return response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
    }
}
