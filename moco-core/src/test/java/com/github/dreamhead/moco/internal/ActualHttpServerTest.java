package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.AbstractMocoHttpTest;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.Runnable;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.context;
import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.fileRoot;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.httpsServer;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.Moco.uri;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteHttpsUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ActualHttpServerTest extends AbstractMocoHttpTest {
    private HttpServer httpServer;
    private HttpServer anotherServer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        httpServer = httpServer(12306, context("/foo"));
        httpServer.response("foo");
        anotherServer = httpServer(12306, context("/bar"));
    }

    @Test
    public void should_merge_http_server_with_any_handler_one_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo/anything")), is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_for_merging_http_server_with_any_handler_one_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(remoteUrl("/bar/anything"));
            }
        });
    }

    @Test
    public void should_merge_http_server_with_any_handler_other_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) httpServer).mergeServer((ActualHttpServer) anotherServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo/anything")), is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_for_merging_http_server_with_any_handler_other_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) httpServer).mergeServer((ActualHttpServer) anotherServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(remoteUrl("/bar/anything"));
            }
        });
    }

    @Test
    public void should_config_handler_correctly_while_merging() throws Exception {
        httpServer = httpServer(12306, fileRoot("src/test/resources"));
        httpServer.response(file("foo.response"));
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);

        running(mergedServer, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    @Test
    public void should_config_handler_correctly_other_side_while_merging() throws Exception {
        httpServer = httpServer(12306, fileRoot("src/test/resources"));
        httpServer.response(file("foo.response"));
        HttpServer mergedServer = ((ActualHttpServer) httpServer).mergeServer((ActualHttpServer) anotherServer);

        running(mergedServer, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    private final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");

    @Test
    public void should_merge_https_server() throws Exception {
        anotherServer = httpsServer(12306, DEFAULT_CERTIFICATE, context("/bar"));
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteHttpsUrl("/foo/anything")), is("foo"));
            }
        });
    }

    @Test
    public void should_merge_two_https_servers() throws Exception {
        httpServer = httpsServer(12306, DEFAULT_CERTIFICATE, context("/foo"));
        httpServer.response("foo");
        anotherServer = httpsServer(12306, DEFAULT_CERTIFICATE, context("/bar"));
        anotherServer.request(by(uri("/bar"))).response("bar");
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteHttpsUrl("/foo/anything")), is("foo"));
                assertThat(helper.get(remoteHttpsUrl("/bar/bar")), is("bar"));
            }
        });
    }

    @Test
    public void should_merge_https_server_into_http_server() throws Exception {
        httpServer = httpsServer(12306, DEFAULT_CERTIFICATE, context("/foo"));
        httpServer.response("foo");
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteHttpsUrl("/foo/anything")), is("foo"));
            }
        });
    }

    @Test
    public void should_merge_http_server_with_same_port() throws Exception {
        httpServer = httpServer(12306, context("/foo"));
        anotherServer = httpServer(12306, context("/bar"));
        final HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(mergedServer.port(), is(12306));
            }
        });
    }

    @Test
    public void should_merge_http_server_with_different_port() throws Exception {
        httpServer = httpServer(12306, context("/foo"));
        anotherServer = httpServer(12307, context("/bar"));
        final HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(mergedServer.port(), is(12307));
            }
        });
    }

    @Test
    public void should_merge_http_server_without_port_for_first_server() throws Exception {
        httpServer = httpServer(12306, context("/foo"));
        anotherServer = httpServer(context("/bar"));
        final HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(mergedServer.port(), is(12306));
            }
        });
    }

    @Test
    public void should_merge_http_server_without_port_for_second_server() throws Exception {
        httpServer = httpServer(context("/foo"));
        anotherServer = httpServer(12307, context("/bar"));
        final HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(mergedServer.port(), is(12307));
            }
        });
    }

    @Test
    public void should_merge_http_server_without_port_for_both_servers() throws Exception {
        httpServer = httpServer(context("/foo"));
        anotherServer = httpServer(context("/bar"));
        final ActualHttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeServer((ActualHttpServer) httpServer);
        assertThat(mergedServer.getPort().isPresent(), is(false));
    }
}
