package com.github.dreamhead.moco;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.github.dreamhead.moco.Runner.running;
import static org.apache.commons.lang.time.DateUtils.addMonths;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoTemplateTest extends AbstractMocoTest {
    @Test
    public void should_generate_response_with_http_method() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.method}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/template")), is("GET"));
                assertThat(helper.postContent(remoteUrl("/template"), "foo"), is("POST"));
            }
        });
    }

    @Test
    public void should_generate_response_with_http_version() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.version}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = Request.Get(remoteUrl("/template")).version(HttpVersion.HTTP_1_0).execute().returnContent().asString();
                assertThat(response, is("HTTP/1.0"));
            }
        });
    }

    @Test
    public void should_generate_response_with_content() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.content}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.postContent(remoteUrl("/template"), "foo"), is("foo"));
            }
        });
    }

    @Test
    public void should_generate_response_with_http_header() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.headers[\"foo\"]}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = Request.Get(remoteUrl("/template")).addHeader("foo", "bar").version(HttpVersion.HTTP_1_0).execute().returnContent().asString();
                assertThat(response, is("bar"));
            }
        });
    }

    @Test
    public void should_generate_response_with_http_query() throws Exception {
        server.request(by(uri("/template"))).response(template("${req.queries[\"foo\"]}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = Request.Get(remoteUrl("/template?foo=bar")).version(HttpVersion.HTTP_1_0).execute().returnContent().asString();
                assertThat(response, is("bar"));
            }
        });
    }

    @Test
    public void should_generate_response_from_file() throws Exception {
        server.request(by(uri("/template"))).response(template(file("src/test/resources/foo.template")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/template")), is("GET"));
            }
        });
    }

    @Test
    public void should_generate_response_version() throws Exception {
        server.request(by(uri("/template"))).response(version(template("${req.version}")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                ProtocolVersion version = Request.Get(remoteUrl("/template")).version(HttpVersion.HTTP_1_0).execute().returnResponse().getProtocolVersion();
                assertThat(version.toString(), is("HTTP/1.0"));
            }
        });
    }

    @Test
    public void should_generate_response_header() throws Exception {
        server.request(by(uri("/template"))).response(header("foo", template("${req.method}")));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = Request.Get(remoteUrl("/template")).version(HttpVersion.HTTP_1_0).execute().returnResponse().getFirstHeader("foo");
                assertThat(header.getValue(), is("GET"));
            }
        });
    }

    @Test
    public void should_generate_response_today_date() throws Exception {
    	final String dateFormat= "yyyy-MM-dd";
    	server.request(by(uri("/template"))).response(template("${req.content} - ${now?string('" + dateFormat +"')}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                
				String today = DateFormatUtils.format(new Date(), dateFormat);
            	assertThat(helper.postContent(remoteUrl("/template"), "foo"), is("foo - "+today));
            }
        });
    }
    
    @Test
    public void should_generate_response_using_date_utils() throws Exception {
    	final String dateFormat= "yyyy-MM-dd";
    	server.request(by(uri("/template"))).response(template("${req.content} - ${dateUtils.addMonths(now, -3)?string('" + dateFormat +"')}"));

        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                
				String today = DateFormatUtils.format(addMonths(new Date(), -3), dateFormat);
            	assertThat(helper.postContent(remoteUrl("/template"), "foo"), is("foo - "+today));
            }
        });
    }

    
    @Test
    public void should_set_and_recognize_cookie() throws Exception {
        server.request(eq(cookie("loggedIn"), "GET")).response(status(200));
        server.response(cookie("loggedIn", template("${req.method}")), status(302));

        running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                int statusBeforeLogin = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                assertThat(statusBeforeLogin, is(302));
                int statusAfterLogin = Request.Get(root()).execute().returnResponse().getStatusLine().getStatusCode();
                assertThat(statusAfterLogin, is(200));
            }
        });
    }
}
