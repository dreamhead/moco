package com.github.dreamhead.moco.handler.failover;

import com.github.dreamhead.moco.model.DefaultRequest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultRequestTest {
    @Test
    public void should_be_match_if_request_is_same() {
        DefaultRequest request = new DefaultRequest();
        request.setVersion("HTTP/1.1");
        request.setMethod("POST");
        request.setContent("proxy");
        request.addHeader("Cookie", "loggedIn=true");
        request.addHeader("Host", "localhost:12306");

        assertThat(request.match(request), is(true));
    }

    @Test
    public void should_be_match_if_failover_field_is_null() {
        DefaultRequest request = new DefaultRequest();
        request.setVersion("HTTP/1.1");
        request.setMethod("POST");
        request.setContent("proxy");
        request.addHeader("Cookie", "loggedIn=true");
        request.addHeader("Host", "localhost:12306");

        DefaultRequest failover = new DefaultRequest();
        failover.setVersion(null);
        failover.setMethod("POST");
        failover.setContent("proxy");
        failover.addHeader("Cookie", "loggedIn=true");
        failover.addHeader("Host", "localhost:12306");

        assertThat(failover.match(failover), is(true));
    }

    @Test
    public void should_be_match_even_if_target_request_has_more_headers() {
        DefaultRequest request = new DefaultRequest();
        request.setVersion("HTTP/1.1");
        request.setMethod("POST");
        request.setContent("proxy");
        request.addHeader("Cookie", "loggedIn=true");
        request.addHeader("Host", "localhost:12306");

        DefaultRequest failover = new DefaultRequest();
        failover.setVersion("HTTP/1.1");
        failover.setMethod("POST");
        failover.setContent("proxy");
        failover.addHeader("Host", "localhost:12306");

        assertThat(failover.match(request), is(true));
    }
}
