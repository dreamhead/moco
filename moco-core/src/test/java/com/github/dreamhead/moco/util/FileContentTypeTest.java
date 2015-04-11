package com.github.dreamhead.moco.util;

import com.google.common.base.Optional;
import org.junit.Test;

import java.nio.charset.Charset;

import static com.google.common.base.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileContentTypeTest {
	private static final String DEFAULT_CONTENT_TYPE = "text/plain; charset=UTF-8";
	private static final String PNG_CONTENT_TYPE = "image/png";

	@Test
	public void should_get_type_from_filename() {
		FileContentType contentType = new FileContentType("logo.png", Optional.<Charset>absent());
		assertThat(contentType.getContentType(), is(PNG_CONTENT_TYPE));
	}

	@Test
	public void should_get_default_type_from_unknown_name() {
		FileContentType contentType = new FileContentType("UNKNOWN_FILE", Optional.<Charset>absent());
		assertThat(contentType.getContentType(), is(DEFAULT_CONTENT_TYPE));
	}

	@Test
	public void should_have_charset_for_file() {
		FileContentType contentType = new FileContentType("result.response", of(Charset.forName("GBK")));
		assertThat(contentType.getContentType(), is("text/plain; charset=GBK"));
	}
}
