package com.github.dreamhead.moco.dumper;

import com.github.dreamhead.moco.HttpMessage;
import com.github.dreamhead.moco.model.DefaultHttpResponse;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;
import io.netty.util.internal.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.dreamhead.moco.dumper.HttpDumpers.asContent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpDumpersTest {

    private static final String MESSAGE_BODY = "test message body";
    private static final String EXPECTED_MESSAGE_BODY = StringUtil.NEWLINE + StringUtil.NEWLINE + MESSAGE_BODY;
    private static final String BINARY_CONTENT_MESSAGE = StringUtil.NEWLINE + StringUtil.NEWLINE + "<content is binary>";

    @Test
    public void should_parse_plain_text_media_type() {
        assertMessageContent("text/html", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_complete_text_media_type() {
        assertMessageContent("text/html; charset=ISO-8859-1", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_plain_json_media_type() {
        assertMessageContent("application/json", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_complete_json_media_type() {
        assertMessageContent("application/json; charset=ISO-8859-1", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_plain_javascript_media_type() {
        assertMessageContent("text/javascript", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_complete_javascript_media_type() {
        assertMessageContent("text/javascript; charset=UTF-8", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_plain_xml_media_type() {
        assertMessageContent("application/xml", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_complete_xml_media_type() {
        assertMessageContent("application/rss+xml", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_not_parse_binary_media_type() {
        assertMessageContent("image/jpeg", BINARY_CONTENT_MESSAGE);
    }

    @Test
    public void should_parse_complete_form_urlencoded_media_type() {
        assertMessageContent("application/x-www-form-urlencoded;charset=UTF-8", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_css_media_type() {
        assertMessageContent("text/css", EXPECTED_MESSAGE_BODY);
    }

    @Test
    public void should_parse_content_when_content_length_not_set() {
        assertThat(asContent(messageWithHeaders(ImmutableMap.of(HttpHeaders.CONTENT_TYPE, "text/plain"))), is(EXPECTED_MESSAGE_BODY));
    }

    @Test
    public void should_not_parse_content_when_content_length_not_set() {
        assertThat(asContent(DefaultHttpResponse.builder()
                .withHeaders(ImmutableMap.of(HttpHeaders.CONTENT_TYPE, "text/plain"))
                .withStringContent("")
                .build()), is(""));
    }

    private void assertMessageContent(final String mediaType, final String expectedContent) {
        assertThat(asContent(messageWithHeaders(defaultHeadersFor(mediaType))), is(expectedContent));
    }

    private HttpMessage messageWithHeaders(final Map<String, String> headers) {
        return DefaultHttpResponse.builder()
                .withHeaders(headers)
                .withStringContent(MESSAGE_BODY)
                .build();
    }

    private Map<String, String> defaultHeadersFor(final String mediaType) {
        return ImmutableMap.<String, String>builder()
                .put(HttpHeaders.CONTENT_LENGTH, String.valueOf(MESSAGE_BODY.length()))
                .put(HttpHeaders.CONTENT_TYPE, mediaType)
                .build();
    }
}
