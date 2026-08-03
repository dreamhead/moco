package com.github.dreamhead.moco.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the wire rendering of every constant and of parse round-trips, because
 * {@code MediaType.toString()} becomes the {@code Content-Type} response header. These values were
 * captured from Guava 33.6.0-jre so the migration cannot silently change the protocol.
 */
public class MediaTypeTest {
    @Test
    public void should_render_constants_exactly_as_guava_did() {
        assertThat(MediaType.PLAIN_TEXT_UTF_8.toString(), is("text/plain; charset=utf-8"));
        assertThat(MediaType.PNG.toString(), is("image/png"));
        assertThat(MediaType.JPEG.toString(), is("image/jpeg"));
        assertThat(MediaType.GIF.toString(), is("image/gif"));
        assertThat(MediaType.TIFF.toString(), is("image/tiff"));
        assertThat(MediaType.PDF.toString(), is("application/pdf"));
        assertThat(MediaType.ZIP.toString(), is("application/zip"));
        assertThat(MediaType.TAR.toString(), is("application/x-tar"));
        assertThat(MediaType.GZIP.toString(), is("application/x-gzip"));
        assertThat(MediaType.APPLICATION_BINARY.toString(), is("application/binary"));
        assertThat(MediaType.FORM_DATA.toString(), is("application/x-www-form-urlencoded"));
    }

    @Test
    public void should_expose_type_and_subtype() {
        assertThat(MediaType.TAR.type(), is("application"));
        assertThat(MediaType.TAR.subtype(), is("x-tar"));
        assertThat(MediaType.PLAIN_TEXT_UTF_8.type(), is("text"));
        assertThat(MediaType.PLAIN_TEXT_UTF_8.subtype(), is("plain"));
    }

    @Test
    public void should_lower_case_type_and_subtype() {
        assertThat(MediaType.create("TEXT", "HTML").toString(), is("text/html"));
    }

    @Test
    public void should_lower_case_charset_on_the_wire() {
        assertThat(MediaType.create("text", "html").withCharset(StandardCharsets.UTF_8).toString(),
                is("text/html; charset=utf-8"));
    }

    @Test
    public void should_replace_charset_rather_than_appending() {
        MediaType type = MediaType.PLAIN_TEXT_UTF_8.withCharset(StandardCharsets.ISO_8859_1);
        assertThat(type.toString(), is("text/plain; charset=iso-8859-1"));
    }

    @Test
    public void should_expose_charset() {
        assertThat(MediaType.PLAIN_TEXT_UTF_8.charset().orElse(null), is(StandardCharsets.UTF_8));
        assertTrue(MediaType.PNG.charset().isEmpty());
    }

    @Test
    public void should_parse_content_type_without_parameter() {
        assertThat(MediaType.parse("text/plain"), is(MediaType.create("text", "plain")));
    }

    @Test
    public void should_parse_content_type_with_spaced_charset() {
        MediaType type = MediaType.parse("text/html; charset=ISO-8859-1");
        assertThat(type.type(), is("text"));
        assertThat(type.subtype(), is("html"));
        assertThat(type.charset().orElse(null), is(StandardCharsets.ISO_8859_1));
        assertThat(type.toString(), is("text/html; charset=iso-8859-1"));
    }

    @Test
    public void should_parse_content_type_without_space_before_charset() {
        MediaType type = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
        assertThat(type.subtype(), is("x-www-form-urlencoded"));
        assertThat(type.charset().orElse(null), is(StandardCharsets.UTF_8));
        assertThat(type.toString(), is("application/x-www-form-urlencoded; charset=utf-8"));
    }

    @Test
    public void should_keep_non_charset_parameters() {
        // A recorded multipart request replays its boundary as the response content type.
        MediaType type = MediaType.parse("multipart/form-data; boundary=--abc123");
        assertThat(type.toString(), is("multipart/form-data; boundary=--abc123"));
    }

    @Test
    public void should_quote_parameter_value_that_is_not_a_token() {
        MediaType type = MediaType.parse("multipart/form-data; boundary=\"a;b\"");
        assertThat(type.toString(), is("multipart/form-data; boundary=\"a;b\""));
    }

    @Test
    public void should_reject_malformed_content_type() {
        // HttpDumpers.isText and InMemoryRequestRecorder rely on this throw as their fallback.
        assertThrows(IllegalArgumentException.class, () -> MediaType.parse("garbage"));
        assertThrows(IllegalArgumentException.class, () -> MediaType.parse("/plain"));
        assertThrows(IllegalArgumentException.class, () -> MediaType.parse("text/"));
        assertThrows(IllegalArgumentException.class, () -> MediaType.parse("text/pl/ain"));
        assertThrows(IllegalArgumentException.class, () -> MediaType.create("text", "pl ain"));
    }

    @Test
    public void should_tolerate_surrounding_whitespace() {
        // Deliberately more lenient than Guava, which rejected trailing whitespace outright.
        // Netty trims header values, so this only ever avoids a spurious fallback.
        assertThat(MediaType.parse("text/plain;  charset=utf-8  ").toString(), is("text/plain; charset=utf-8"));
    }

    @Test
    public void should_be_equal_regardless_of_how_it_was_built() {
        Charset gbk = Charset.forName("gbk");
        assertThat(MediaType.create("text", "plain").withCharset(gbk),
                is(MediaType.PLAIN_TEXT_UTF_8.withCharset(gbk)));
        assertThat(MediaType.parse("text/plain; charset=utf-8"), is(MediaType.PLAIN_TEXT_UTF_8));
        assertThat(MediaType.parse("text/plain; charset=utf-8").hashCode(),
                is(MediaType.PLAIN_TEXT_UTF_8.hashCode()));
    }
}
