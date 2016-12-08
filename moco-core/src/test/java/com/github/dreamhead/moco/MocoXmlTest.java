package com.github.dreamhead.moco;

import org.apache.http.client.HttpResponseException;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.exist;
import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.text;
import static com.github.dreamhead.moco.Moco.xml;
import static com.github.dreamhead.moco.Moco.xpath;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoXmlTest extends AbstractMocoHttpTest {
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

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_for_mismatch_xpath() throws Exception {
        server.request(eq(xpath("/request/parameters/id/text()"), "3")).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.postFile(root(), "foo.xml");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_for_unknown_xpath() throws Exception {
        server.request(eq(xpath("/response/parameters/id/text()"), "3")).response("foo");
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.postFile(root(), "foo.xml");
            }
        });
    }

    @Test
    public void should_return_content_based_on_xpath_with_many_elements() throws Exception {
        server.request(eq(xpath("/request/parameters/id/text()"), "2")).response("bar");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postFile(root(), "foobar.xml"), is("bar"));
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
        server.request(xml("<request><parameters><id>1</id></parameters></request>")).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postFile(root(), "foo.xml"), is("foo"));
            }
        });
    }

    @Test
    public void should_match_xml_with_resource() throws Exception {
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
        server.request(xml("<request><parameters><id>1</id></parameters></request>")).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(root(), "blah");
            }
        });
    }

    @Test
    public void should_return_content_based_on_xpath_existing() throws Exception {
        server.request(exist(xpath("/request/parameters/id/text()"))).response("foo");

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.postFile(root(), "foo.xml"), is("foo"));
            }
        });
    }
}
