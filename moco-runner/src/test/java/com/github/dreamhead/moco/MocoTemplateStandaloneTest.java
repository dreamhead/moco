package com.github.dreamhead.moco;

import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.RemoteTestUtils.remoteUrl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoTemplateStandaloneTest extends AbstractMocoStandaloneTest {
    @Test
    public void should_return_content_with_template() throws IOException {
        runWithConfiguration("template.json");
        assertThat(helper.get(remoteUrl("/template")), is("GET"));
    }

    @Test
    public void should_return_content_from_file_template() throws IOException {
        runWithConfiguration("template.json");
        assertThat(helper.get(remoteUrl("/file_template")), is("GET"));
    }

    @Test
    public void should_return_content_from_path_resource_template() throws IOException {
        runWithConfiguration("template.json");
        assertThat(helper.get(remoteUrl("/file_template")), is("GET"));
    }

    @Test
    public void should_return_version_from_template() throws IOException {
        runWithConfiguration("template.json");
        ProtocolVersion version = Request.Get(remoteUrl("/version_template")).version(HttpVersion.HTTP_1_0).execute().returnResponse().getProtocolVersion();
        assertThat(version.toString(), is("HTTP/1.0"));
    }

    @Test
    public void should_return_header_from_template() throws IOException {
        runWithConfiguration("template.json");
        Header header = Request.Get(remoteUrl("/header_template")).addHeader("foo", "bar").execute().returnResponse().getFirstHeader("foo");
        assertThat(header.getValue(), is("bar"));
    }

    @Test
    public void should_return_cookie_from_template() throws IOException {
        runWithConfiguration("template.json");
        Request.Get(remoteUrl("/cookie_template")).execute();
        String content = helper.get(remoteUrl("/cookie_template"));
        assertThat(content, is("GET"));
    }

    @Test
    public void should_return_form_value_from_template() throws IOException {
        runWithConfiguration("template.json");
        String content = Request.Post(remoteUrl("/form_template")).bodyForm(new BasicNameValuePair("foo", "dreamhead")).execute().returnContent().asString();
        assertThat(content, is("dreamhead"));
    }
}
