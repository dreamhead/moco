package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoRequestHit.*;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MocoRequestHitTest {
    private MocoTestHelper helper;
    private RequestHit hit;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
        hit = requestHit();
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
        httpserver(port(), hit);
        hit.verify(by(uri("/foo")), times(1));
    }

    @Test
    public void should_verify_expected_request_for_at_least() throws Exception {
        final HttpServer server = httpserver(port(), hit);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        hit.verify(by(uri("/foo")), atLeast(1));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_at_least_expected_request_while_expectation_can_not_be_met() throws Exception {
        httpserver(port(), hit);
        hit.verify(by(uri("/foo")), atLeast(1));
    }

    @Test
    public void should_verify_expected_request_for_at_most() throws Exception {
        final HttpServer server = httpserver(port(), hit);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        hit.verify(by(uri("/foo")), atMost(2));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_at_most_expected_request_while_expectation_can_not_be_met() throws Exception {
        final HttpServer server = httpserver(port(), hit);
        server.get(by(uri("/foo"))).response("bar");
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        hit.verify(by(uri("/foo")), atMost(1));
    }

    @Test
    public void should_verify_expected_request_for_once() throws Exception {
        final HttpServer server = httpserver(port(), hit);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        hit.verify(by(uri("/foo")), once());
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_while_once_expectation_can_not_be_met() throws Exception {
        httpserver(port(), hit);
        hit.verify(by(uri("/foo")), times(1));
    }

    @Test
    public void should_verify_unexpected_request_without_unexpected_request() throws Exception {
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
        final HttpServer server = httpserver(port(), hit);

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                try {
                    helper.get(remoteUrl("/foo"));
                } catch (IOException ignored) {
                }
            }
        });

        hit.verify(unexpected(), times(1));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_while_unexpected_request_expectation_can_not_be_met() throws Exception {
        final HttpServer server = httpserver(port(), hit);

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                try {
                    helper.get(remoteUrl("/foo"));
                } catch (IOException ignored) {
                }
            }
        });

        hit.verify(unexpected(), never());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_negative_number_for_times() {
        times(-1);
    }
}
