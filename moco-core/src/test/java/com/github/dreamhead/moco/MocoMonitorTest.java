package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MocoMonitorTest {
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
    }

    @Test
    public void should_monitor_server_behavior() throws Exception {
        final MocoMonitor monitor = mock(MocoMonitor.class);
        final HttpServer server = httpserver(port(), monitor);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        verify(monitor).onMessageArrived(any(FullHttpRequest.class));
        verify(monitor).onMessageLeave(any(FullHttpResponse.class));
        verify(monitor, Mockito.never()).onException(any(Exception.class));
    }

    @Test
    public void should_monitor_server_behavior_without_port() throws Exception {
        final MocoMonitor monitor = mock(MocoMonitor.class);
        final HttpServer server = httpserver(monitor);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl(server.port(), "/foo")), is("bar"));
            }
        });

        verify(monitor).onMessageArrived(any(FullHttpRequest.class));
        verify(monitor).onMessageLeave(any(FullHttpResponse.class));
        verify(monitor, Mockito.never()).onException(any(Exception.class));
    }

    @Test
    public void should_verify_expected_request() throws Exception {
        final RequestHit hit = requestHit();
        final HttpServer server = httpserver(port(), hit);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        hit.verify(by(uri("/foo")), times(1));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_while_expectation_can_not_be_met() throws Exception {
        final RequestHit hit = requestHit();
        final HttpServer server = httpserver(port(), hit);
        hit.verify(by(uri("/foo")), times(1));
    }

    @Test
    public void should_verify_unexpected_request_without_unexpected_request() throws Exception {
        final RequestHit hit = requestHit();
        final HttpServer server = httpserver(port(), hit);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        hit.verify(unexpected(), never());
    }

    @Test
    public void should_verify_unexpected_request_with_unexpected_request() throws Exception {
        final RequestHit hit = requestHit();
        final HttpServer server = httpserver(port(), hit);

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                try {
                    helper.get(remoteUrl("/foo"));
                } catch (IOException e) {
                }
            }
        });

        hit.verify(unexpected(), times(1));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_while_unexpected_request_expectation_can_not_be_met() throws Exception {
        final RequestHit hit = requestHit();
        final HttpServer server = httpserver(port(), hit);

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                try {
                    helper.get(remoteUrl("/foo"));
                } catch (IOException e) {
                }
            }
        });

        hit.verify(unexpected(), never());
    }
}
