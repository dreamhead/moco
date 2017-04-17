package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.form;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.httpsServer;
import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.MocoRequestHit.atLeast;
import static com.github.dreamhead.moco.MocoRequestHit.atMost;
import static com.github.dreamhead.moco.MocoRequestHit.between;
import static com.github.dreamhead.moco.MocoRequestHit.never;
import static com.github.dreamhead.moco.MocoRequestHit.once;
import static com.github.dreamhead.moco.MocoRequestHit.requestHit;
import static com.github.dreamhead.moco.MocoRequestHit.times;
import static com.github.dreamhead.moco.MocoRequestHit.unexpected;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteHttpsUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MocoRequestHitTest {
    private static final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");

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
        final HttpServer server = httpServer(port(), monitor);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        verify(monitor).onMessageArrived(any(HttpRequest.class));
        verify(monitor).onMessageLeave(any(HttpResponse.class));
        verify(monitor, Mockito.never()).onException(any(Exception.class));
    }

    @Test
    public void should_monitor_server_behavior_without_port() throws Exception {
        final MocoMonitor monitor = mock(MocoMonitor.class);
        final HttpServer server = httpServer(monitor);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl(server.port(), "/foo")), is("bar"));
            }
        });

        verify(monitor).onMessageArrived(any(HttpRequest.class));
        verify(monitor).onMessageLeave(any(HttpResponse.class));
        verify(monitor, Mockito.never()).onException(any(Exception.class));
    }

    @Test
    public void should_verify_expected_request() throws Exception {
        final HttpServer server = httpServer(port(), hit);
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
        httpServer(port(), hit);
        hit.verify(by(uri("/foo")), times(1));
    }

    @Test
    public void should_verify_expected_request_for_at_least() throws Exception {
        final HttpServer server = httpServer(port(), hit);
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

    @Test
    public void should_verify_expected_request_for_between() throws Exception {
        final HttpServer server = httpServer(port(), hit);
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        hit.verify(by(uri("/foo")), between(1, 3));
    }

    @Test(expected = VerificationException.class)
    public void should_fail_to_verify_at_least_expected_request_while_expectation_can_not_be_met() throws Exception {
        httpServer(port(), hit);
        hit.verify(by(uri("/foo")), atLeast(1));
    }

    @Test
    public void should_verify_expected_request_for_at_most() throws Exception {
        final HttpServer server = httpServer(port(), hit);
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
        final HttpServer server = httpServer(port(), hit);
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
        final HttpServer server = httpServer(port(), hit);
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
        httpServer(port(), hit);
        hit.verify(by(uri("/foo")), times(1));
    }

    @Test
    public void should_verify_unexpected_request_without_unexpected_request() throws Exception {
        final HttpServer server = httpServer(port(), hit);
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
        final HttpServer server = httpServer(port(), hit);

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
        final HttpServer server = httpServer(port(), hit);

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

    @Test
    public void should_verify_form_data() throws Exception {
        final HttpServer server = httpServer(port(), hit);
        server.post(eq(form("name"), "dreamhead")).response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Request request = Request.Post(root()).bodyForm(new BasicNameValuePair("name", "dreamhead"));
                String content = helper.executeAsString(request);
                assertThat(content, is("foobar"));
            }
        });

        hit.verify(eq(form("name"), "dreamhead"), once());
    }

    @Test
    public void should_verify_form_data_even_if_no_server_expectation() throws Exception {
        final HttpServer server = httpServer(port(), hit);
        server.response("foobar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Request request = Request.Post(root()).bodyForm(new BasicNameValuePair("name", "dreamhead"));
                String content = helper.executeAsString(request);
                assertThat(content, is("foobar"));
            }
        });

        hit.verify(eq(form("name"), "dreamhead"), once());
    }

    @Test
    public void should_verify_expected_request_and_log_at_same_time() throws Exception {
        final HttpServer server = httpServer(port(), hit, log());
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo")), is("bar"));
            }
        });

        hit.verify(by(uri("/foo")), times(1));
    }

    @Test
    public void should_verify_expected_request_and_log_at_same_time_for_https() throws Exception {
        final HttpServer server = httpsServer(port(), DEFAULT_CERTIFICATE, hit, log());
        server.get(by(uri("/foo"))).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteHttpsUrl("/foo")), is("bar"));
            }
        });

        hit.verify(by(uri("/foo")), times(1));
    }
}
