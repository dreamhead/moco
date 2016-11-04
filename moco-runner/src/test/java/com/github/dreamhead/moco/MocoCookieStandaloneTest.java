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
        org.apache.http.HttpResponse response = helper.getResponse(remoteUrl("/cookie"));

        String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
        Cookie decodeCookie = ClientCookieDecoder.STRICT.decode(value);
        assertThat(decodeCookie.name(), is("login"));
        assertThat(decodeCookie.value(), is("true"));
    }

    @Test
    public void should_set_and_recognize_cookie_with_path() throws IOException {
        runWithConfiguration("cookie.json");
        org.apache.http.HttpResponse response = helper.getResponse(remoteUrl("/cookie-with-path"));

        String value = response.getFirstHeader(HttpHeaders.SET_COOKIE).getValue();
        Cookie decodeCookie = ClientCookieDecoder.STRICT.decode(value);
        assertThat(decodeCookie.name(), is("login"));
        assertThat(decodeCookie.value(), is("true"));
        assertThat(decodeCookie.path(), is("/"));
    }
}
