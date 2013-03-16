package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.port;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoXmlTest {
    private HttpServer server;
    private MocoTestHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new MocoTestHelper();
        server = httpserver(port());
    }

    @Test
    public void should_return_content_based_on_xpath() throws Exception {
        server.request(eq(xpath("/request/parameters/id/text()"), "1")).response("foo");
        server.request(eq(xpath("/request/parameters/id/text()"), "2")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postFile(root(), "foo.xml"), is("foo"));
                assertThat(helper.postFile(root(), "bar.xml"), is("bar"));
            }
        });
    }

    @Test
    public void should_match_exact_xml() throws Exception {
        server.request(xml(file("src/test/resources/foo.xml"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postFile(root(), "foo.xml"), is("foo"));
            }
        });
    }

    @Test
    public void should_match_xml() throws Exception {
        server.request(xml(text("<request><parameters><id>1</id></parameters></request>"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postFile(root(), "foo.xml"), is("foo"));
            }
        });
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_unknown_content() throws Exception {
        server.request(xml(text("<request><parameters><id>1</id></parameters></request>"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(root(), "blah");
            }
        });
    }
}
