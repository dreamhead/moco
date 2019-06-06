package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.asHeader;
import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.fileRoot;
import static com.github.dreamhead.moco.Moco.header;
import static com.github.dreamhead.moco.Moco.httpServer;
import static com.github.dreamhead.moco.Moco.log;
import static com.github.dreamhead.moco.Moco.template;
import static com.github.dreamhead.moco.MocoMount.to;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.port;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoFileRootTest {
    private HttpServer server;
    private MocoTestHelper helper;

    @Before
    public void setup() {
        helper = new MocoTestHelper();
        server = httpServer(port(), fileRoot("src/test/resources"));
    }

    @Test
    public void should_config_file_root() throws Exception {
        server.response(file("foo.response"));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_header_from_file_root() throws Exception {
        server = httpServer(port(), log(), fileRoot("src/test/resources"));
        server.response(asHeader("foo", file("foo.response")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Get(root());
                Header header = helper.execute(request).getFirstHeader("foo");

                assertThat(header.getValue(), is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_template_header_from_file_root() throws Exception {
        server.response(header("foo", template(file("foo.response"))));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                org.apache.http.HttpResponse response = helper.getResponse(root());
                Header header = response.getFirstHeader("foo");
                assertThat(header.getValue(), is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_template_from_file_root() throws Exception {
        server.response(template(file("foo.response")));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    @Test
    public void should_mount_correctly() throws Exception {
        server.mount("test", to("/dir"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/dir/dir.response")), is("response from dir"));
            }
        });
    }
}
