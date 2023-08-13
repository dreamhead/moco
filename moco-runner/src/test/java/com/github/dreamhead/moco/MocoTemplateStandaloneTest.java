package com.github.dreamhead.moco;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.github.dreamhead.moco.helper.RemoteTestUtils.remoteUrl;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.root;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

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
        ProtocolVersion version = helper.execute(Request.get(remoteUrl("/version_template"))
                .version(HttpVersion.HTTP_1_0))
                .getVersion();
        assertThat(version.toString(), is("HTTP/1.0"));
    }

    @Test
    public void should_return_header_from_template() throws IOException {
        runWithConfiguration("template.json");
        Header header = helper.execute(Request.get(remoteUrl("/header_template")).addHeader("foo", "bar"))
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
        Request request = Request.post(remoteUrl("/form_template")).bodyForm(new BasicNameValuePair("foo", "dreamhead"));
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
    public void should_not_return_json_field_from_template_for_unknown_content() throws IOException {
        runWithConfiguration("template.json");
        final HttpResponse response = helper.postForResponse(remoteUrl("/json_template"), "blah");
        assertThat(response.getCode(), is(400));
    }

    @Test
    public void should_return_xml_field_from_template() throws IOException {
        runWithConfiguration("template.json");
        String content = helper.postContent(remoteUrl("/xml_template"), "<xml><foo>bar</foo></xml>");
        assertThat(content, is("bar"));
    }

    @Test
    public void should_not_return_xml_field_from_template_for_unknown_content() throws IOException {
        runWithConfiguration("template.json");
        final HttpResponse response = helper.postForResponse(remoteUrl("/xml_template"), "blah");
        assertThat(response.getCode(), is(400));
    }

    @Test
    public void should_return_client_address_from_template() throws IOException {
        runWithConfiguration("template.json");
        String content = helper.get(remoteUrl("/client_address_template"));
        assertThat(content, is("127.0.0.1"));
    }

    @Test
    public void should_return_now_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");

        final ZonedDateTime now = ZonedDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        assertThat(helper.get(remoteUrl("/now_template")), is(formatter.format(now)));
    }

    @Test
    public void should_return_random_with_range_and_format_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_with_range_and_format"));
        double result = Double.parseDouble(response);
        assertThat(result, lessThan(100d));
        assertThat(result, greaterThanOrEqualTo(0d));

        String target = response.split("\\.")[1];
        assertThat(target.length(), lessThanOrEqualTo(6));
    }

    @Test
    public void should_return_random_with_range_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_with_range"));
        double result = Double.parseDouble(response);
        assertThat(result, lessThan(100d));
        assertThat(result, greaterThanOrEqualTo(0d));
    }

    @Test
    public void should_return_random_with_format_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_with_format"));
        String[] result = response.split("\\.");
        String target = result[1];
        assertThat(target.length(), lessThanOrEqualTo(6));
    }

    @Test
    public void should_return_random_without_arg_from_template() throws IOException {
        runWithConfiguration("template_with_function.json");
        String response = helper.get(remoteUrl("/random_template_without_arg"));
        double result = Double.parseDouble(response);
        assertThat(result, lessThanOrEqualTo(1d));
        assertThat(result, greaterThanOrEqualTo(0d));
    }

    @Test
    public void should_return_path_in_template() throws IOException {
        runWithConfiguration("template.json");
        assertThat(helper.get(remoteUrl("/path/path")), is("path"));
        assertThat(helper.get(remoteUrl("/path/foo")), is("foo"));
    }
}
