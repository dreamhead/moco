package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
        MocoMonitor monitor = mock(MocoMonitor.class);
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
        MocoMonitor monitor = mock(MocoMonitor.class);
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
    public void should_verify_unexpected_request() throws Exception {
        RequestHit hit = requestHit();

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
}
