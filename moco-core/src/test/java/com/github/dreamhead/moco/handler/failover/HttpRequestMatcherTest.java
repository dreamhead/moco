package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DumpHttpRequest;
import com.github.dreamhead.moco.model.HttpRequestFailoverMatcher;
import org.junit.Test;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpRequestMatcherTest {
    @Test
    public void should_be_match_if_request_is_same() {
        HttpRequest request = DumpHttpRequest.builder()
                .withVersion("HTTP/1.1")
                .withMethod("POST")
                .withContent("proxy")
                .withHeaders(of("Cookie", "loggedIn=true", "Host", "localhost:12306"))
                .build();

        assertThat(new HttpRequestFailoverMatcher(request).match(request), is(true));
    }

    @Test
    public void should_not_be_match_if_request_is_different() {
        HttpRequest request = DumpHttpRequest.builder()
                .withVersion("HTTP/1.1")
                .withMethod("POST")
                .withContent("proxy")
                .withHeaders(of("Cookie", "loggedIn=true", "Host", "localhost:12306"))
                .build();

        HttpRequest another = DumpHttpRequest.builder()
                .withVersion("HTTP/1.1")
                .withMethod("POST")
                .withContent("different")
                .withHeaders(of("Cookie", "loggedIn=true", "Host", "localhost:12306"))
                .build();

        assertThat(new HttpRequestFailoverMatcher(request).match(another), is(false));
    }

    @Test
    public void should_not_be_match_if_uri_is_different() {
        HttpRequest request = DumpHttpRequest.builder()
                .withVersion("HTTP/1.1")
                .withMethod("POST")
                .withContent("proxy")
                .withUri("/foo")
                .withHeaders(of("Cookie", "loggedIn=true", "Host", "localhost:12306"))
                .build();

        HttpRequest another = DumpHttpRequest.builder()
                .withVersion("HTTP/1.1")
                .withMethod("POST")
                .withContent("proxy")
                .withUri("/bar")
                .withHeaders(of("Cookie", "loggedIn=true", "Host", "localhost:12306"))
                .build();

        assertThat(new HttpRequestFailoverMatcher(request).match(another), is(false));
    }

    @Test
    public void should_be_match_if_failover_field_is_null() {
        HttpRequest request = DumpHttpRequest.builder()
                .withVersion("HTTP/1.1")
                .withMethod("POST")
                .withContent("proxy")
                .withHeaders(of("Cookie", "loggedIn=true", "Host", "localhost:12306"))
                .build();

        HttpRequest failover = DumpHttpRequest.builder()
                .withMethod("POST")
                .withContent("proxy")
                .withHeaders(of("Cookie", "loggedIn=true", "Host", "localhost:12306"))
                .build();

        assertThat(new HttpRequestFailoverMatcher(failover).match(request), is(true));
    }

    @Test
    public void should_be_match_even_if_target_request_has_more_headers() {
        HttpRequest request = DumpHttpRequest.builder()
                .withVersion("HTTP/1.1")
                .withMethod("POST")
                .withContent("proxy")
                .withHeaders(of("Cookie", "loggedIn=true", "Host", "localhost:12306"))
                .build();

        HttpRequest failover = DumpHttpRequest.builder()
                .withVersion("HTTP/1.1")
                .withMethod("POST")
                .withContent("proxy")
                .withHeaders(of("Host", "localhost:12306"))
                .build();

        assertThat(new HttpRequestFailoverMatcher(failover).match(request), is(true));
    }
}
