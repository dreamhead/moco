package com.github.dreamhead.moco;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.RemoteTestUtils.root;
import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class MocoStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_expected_response() throws IOException {
        runWithConfiguration("foo.json");
        assertThat(helper.get(root()), is("foo"));
    }

    @Test
    public void should_return_expected_response_with_file() throws IOException {
        runWithConfiguration("any_response_with_file.json");
        assertThat(helper.get(root()), is("foo.response"));
    }

    @Test
    public void should_return_expected_response_with_text_based_on_specified_uri() throws IOException {
        runWithConfiguration("foo.json");
        assertThat(helper.get(remoteUrl("/foo")), is("bar"));
    }

    @Test
    public void should_return_expected_response_with_file_based_on_specified_request() throws IOException {
        runWithConfiguration("foo.json");
        assertThat(helper.get(remoteUrl("/file")), is("foo.response"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_text_request() throws IOException {
        runWithConfiguration("foo.json");
        assertThat(helper.postContent(root(), "text_request"), is("response_for_text_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_file_request() throws IOException {
        runWithConfiguration("foo.json");
        assertThat(helper.postFile(root(), "foo.request"), is("response_for_file_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_get_request() throws IOException {
        runWithConfiguration("get_method.json");
        assertThat(helper.get(remoteUrl("/get")), is("response_for_get_method"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_while_request_non_get_request() throws IOException {
        runWithConfiguration("get_method.json");
        helper.postContent(remoteUrl("/get"), "");
    }

    @Test
    public void should_return_expected_response_based_on_specified_post_request() throws IOException {
        runWithConfiguration("post_method.json");
        assertThat(helper.postContent(remoteUrl("/post"), ""), is("response_for_post_method"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_while_request_non_post_request() throws IOException {
        runWithConfiguration("post_method.json");
        helper.get(remoteUrl("/post"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_put_request() throws IOException {
        runWithConfiguration("put_method.json");
        String response = Request.Put(remoteUrl("/put")).execute().returnContent().toString();
        assertThat(response, is("response_for_put_method"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_delete_request() throws IOException {
        runWithConfiguration("delete_method.json");
        String response = Request.Delete(remoteUrl("/delete")).execute().returnContent().toString();
        assertThat(response, is("response_for_delete_method"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_version() throws IOException {
        runWithConfiguration("foo.json");
        String response = Request.Get(root()).version(HttpVersion.HTTP_1_0).execute().returnContent().toString();
        assertThat(response, is("version"));
    }

    @Test
    public void should_return_specified_version_for_request() throws IOException {
        runWithConfiguration("foo.json");
        ProtocolVersion version = Request.Get(remoteUrl("/version10")).execute().returnResponse().getProtocolVersion();
        assertThat(version.getProtocol(), is("HTTP"));
        assertThat(version.getMajor(), is(1));
        assertThat(version.getMinor(), is(0));
    }

    @Test
    public void should_return_expected_response_based_on_specified_header_request() throws IOException {
        runWithConfiguration("header.json");
        assertThat(helper.getWithHeader(remoteUrl("/header"), of("content-type", "application/json")), is("response_for_header_request"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_unknown_header() throws IOException {
        runWithConfiguration("header.json");
        helper.get(remoteUrl("/header"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_query_request() throws IOException {
        runWithConfiguration("query.json");
        assertThat(helper.get(remoteUrl("/query?param=foo")), is("response_for_query_request"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_different_query_param() throws IOException {
        runWithConfiguration("query.json");
        helper.get(remoteUrl("/query?param2=foo"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_different_query_param_value() throws IOException {
        runWithConfiguration("query.json");
        helper.get(remoteUrl("/query?param=foo2"));
    }

    @Test
    public void should_expected_response_status_code() throws IOException {
        runWithConfiguration("foo.json");
        assertThat(helper.getForStatus(remoteUrl("/status")), is(200));
    }

    @Test
    public void should_expected_response_header() throws IOException {
        runWithConfiguration("foo.json");
        HttpResponse response = Request.Get(remoteUrl("/response_header")).execute().returnResponse();
        assertThat(response.getHeaders("content-type")[0].getValue(), is("application/json"));
        assertThat(response.getHeaders("foo")[0].getValue(), is("bar"));
    }

    @Test
    public void should_run_as_proxy() throws IOException {
        runWithConfiguration("foo.json");
        HttpResponse response = Request.Get(remoteUrl("/proxy")).execute().returnResponse();
        String value = response.getHeaders("Content-Type")[0].getValue();
        assertThat(value, startsWith("text/html"));
    }

    @Test
    public void should_expected_composite_response() throws IOException {
        runWithConfiguration("foo.json");
        HttpResponse response = Request.Get(remoteUrl("/composite-response")).execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode(), is(200));
        assertThat(response.getHeaders("foo")[0].getValue(), is("bar"));
    }

    @Test
    public void should_set_and_recognize_cookie() throws IOException {
        runWithConfiguration("foo.json");
        helper.get(remoteUrl("/cookie"));
        assertThat(helper.get(remoteUrl("/cookie")), is("success"));
    }

    @Test
    public void should_wait_for_awhile() throws IOException {
        final long latency = 1000;
        final long delta = 200;

        runWithConfiguration("foo.json");
        long start = System.currentTimeMillis();
        int code = helper.getForStatus(remoteUrl("/latency"));
        long stop = System.currentTimeMillis();
        long gap = stop - start + delta;
        assertThat(gap, greaterThan(latency));
        assertThat(code, is(200));
    }

    @Test
    public void should_match_form_value() throws IOException {
        runWithConfiguration("form.json");

        String content = Request.Post(root()).bodyForm(new BasicNameValuePair("name", "dreamhead")).execute().returnContent().asString();
        assertThat(content, is("foobar"));
    }

    @Test
    public void should_have_favicon() throws IOException {
        runWithConfiguration("foo.json");
        String header = Request.Get(remoteUrl("/favicon.ico")).execute().returnResponse().getHeaders("Content-Type")[0].getValue();
        assertThat(header, is("image/png"));
    }
}
