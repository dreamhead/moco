package com.github.dreamhead.moco;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
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
        ProtocolVersion version = helper.execute(Request.Get(remoteUrl("/version_template"))
                .version(HttpVersion.HTTP_1_0))
                .getProtocolVersion();
        assertThat(version.toString(), is("HTTP/1.0"));
    }

    @Test
    public void should_return_header_from_template() throws IOException {
        runWithConfiguration("template.json");
        Header header = helper.execute(Request.Get(remoteUrl("/header_template")).addHeader("foo", "bar"))
                .getFirstHeader("foo");
        assertThat(header.getValue(), is("bar"));
    }

    @Test
    public void should_return_cookie_from_template() throws IOException {
        runWithConfiguration("template.json");
        int status = helper.getForStatus(remoteUrl("/cookie_template"));
        assertThat(status, is(302));

        String content = helper.get(remoteUrl("/cookie_template"));
        assertThat(content, is("GET"));
    }

    @Test
    public void should_return_form_value_from_template() throws IOException {
        runWithConfiguration("template.json");
        Request request = Request.Post(remoteUrl("/form_template")).bodyForm(new BasicNameValuePair("foo", "dreamhead"));
        assertThat(helper.executeAsString(request), is("dreamhead"));
    }

    @Test
    public void should_return_query_value_from_template() throws IOException {
        runWithConfiguration("template.json");
        String content = helper.get(remoteUrl("/query_template?foo=dreamhead"));
        assertThat(content, is("dreamhead"));
    }

    @Test
    public void should_return_template_with_vars() throws IOException {
        runWithConfiguration("template_with_var.json");
        String content = helper.get(remoteUrl("/var_template"));
        assertThat(content, is("another template"));
    }

    @Test
    public void should_return_template_with_extractor() throws IOException {
        runWithConfiguration("template_with_extractor.json");
        String content = helper.postContent(remoteUrl("/extractor_template"), "{\"book\":{\"price\":\"1\"}}");
        assertThat(content, is("1"));
    }

    @Test
    public void should_return_file_with_template_name() throws IOException {
        runWithConfiguration("response_with_template_name.json");
        assertThat(helper.get(root()), is("foo.response"));
    }

    @Test
    public void should_return_json_field_from_template() throws IOException {
        runWithConfiguration("template.json");
        String content = helper.postContent(remoteUrl("/json_template"), "{\"foo\":\"bar\"}");
        assertThat(content, is("bar"));
    }

    @Test
    public void should_return_now_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        assertThat(helper.get(remoteUrl("/now_template")), is(format.format(date)));
    }

    @Test
    public void should_return_random_with_range_and_format_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_with_range_and_format"));
        double result = Double.parseDouble(response);
        assertThat(result, lessThan(100d));
        assertThat(result, greaterThan(0d));

        String target = Iterables.get(Splitter.on('.').split(response), 1);
        assertThat(target.length(), lessThanOrEqualTo(6));
    }

    @Test
    public void should_return_random_with_range_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_with_range"));
        double result = Double.parseDouble(response);
        assertThat(result, lessThan(100d));
        assertThat(result, greaterThan(0d));
    }

    @Test
    public void should_return_random_with_format_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_with_format"));
        String target = Iterables.get(Splitter.on('.').split(response), 1);
        assertThat(target.length(), lessThanOrEqualTo(6));
    }

    @Test
    public void should_return_random_without_arg_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_without_arg"));
        double result = Double.parseDouble(response);
        assertThat(result, lessThan(1d));
        assertThat(result, greaterThan(0d));
    }
}
