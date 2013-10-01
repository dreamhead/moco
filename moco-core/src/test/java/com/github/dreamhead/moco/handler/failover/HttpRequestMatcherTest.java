package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.model.DumpHttpRequest;
import com.github.dreamhead.moco.model.HttpRequestFailoverMatcher;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpRequestMatcherTest {
    @Test
    public void should_be_match_if_request_is_same() {
        DumpHttpRequest request = new DumpHttpRequest();
        request.setVersion("HTTP/1.1");
        request.setMethod("POST");
        request.setContent("proxy");
        request.addHeader("Cookie", "loggedIn=true");
        request.addHeader("Host", "localhost:12306");

        assertThat(new HttpRequestFailoverMatcher(request).match(request), is(true));
    }

    @Test
    public void should_not_be_match_if_request_is_different() {
        DumpHttpRequest request = new DumpHttpRequest();
        request.setVersion("HTTP/1.1");
        request.setMethod("POST");
        request.setContent("proxy");
        request.addHeader("Cookie", "loggedIn=true");
        request.addHeader("Host", "localhost:12306");

        DumpHttpRequest another = new DumpHttpRequest();
        another.setVersion("HTTP/1.1");
        another.setMethod("POST");
        another.setContent("different");
        another.addHeader("Cookie", "loggedIn=true");
        another.addHeader("Host", "localhost:12306");

        assertThat(new HttpRequestFailoverMatcher(request).match(another), is(false));
    }

    @Test
    public void should_not_be_match_if_uri_is_different() {
        DumpHttpRequest request = new DumpHttpRequest();
        request.setVersion("HTTP/1.1");
        request.setMethod("POST");
        request.setContent("proxy");
        request.addHeader("Cookie", "loggedIn=true");
        request.addHeader("Host", "localhost:12306");
        request.setUri("/foo");

        DumpHttpRequest another = new DumpHttpRequest();
        another.setVersion("HTTP/1.1");
        another.setMethod("POST");
        another.setContent("proxy");
        another.addHeader("Cookie", "loggedIn=true");
        another.addHeader("Host", "localhost:12306");
        another.setUri("/bar");

        assertThat(new HttpRequestFailoverMatcher(request).match(another), is(false));
    }

    @Test
    public void should_be_match_if_failover_field_is_null() {
        DumpHttpRequest request = new DumpHttpRequest();
        request.setVersion("HTTP/1.1");
        request.setMethod("POST");
        request.setContent("proxy");
        request.addHeader("Cookie", "loggedIn=true");
        request.addHeader("Host", "localhost:12306");

        DumpHttpRequest failover = new DumpHttpRequest();
        failover.setVersion(null);
        failover.setMethod("POST");
        failover.setContent("proxy");
        failover.addHeader("Cookie", "loggedIn=true");
        failover.addHeader("Host", "localhost:12306");

        assertThat(new HttpRequestFailoverMatcher(failover).match(request), is(true));
    }

    @Test
    public void should_be_match_even_if_target_request_has_more_headers() {
        DumpHttpRequest request = new DumpHttpRequest();
        request.setVersion("HTTP/1.1");
        request.setMethod("POST");
        request.setContent("proxy");
        request.addHeader("Cookie", "loggedIn=true");
        request.addHeader("Host", "localhost:12306");

        DumpHttpRequest failover = new DumpHttpRequest();
        failover.setVersion("HTTP/1.1");
        failover.setMethod("POST");
        failover.setContent("proxy");
        failover.addHeader("Host", "localhost:12306");

        assertThat(new HttpRequestFailoverMatcher(failover).match(request), is(true));
    }
}
