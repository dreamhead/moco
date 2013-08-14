package com.github.dreamhead.moco.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileContentTypeTest {
	private final String DEFAULT_CONTENT_TYPE = "text/plain; charset=UTF-8";
	private final String PNG_CONTENT_TYPE = "image/png";

	@Test
	public void should_get_type_from_filename() {
		FileContentType contentType = new FileContentType("logo.png");
		assertThat(contentType.getContentType(), is(PNG_CONTENT_TYPE));
	}

	@Test
	public void should_get_default_type_from_unknown_name() {
		FileContentType contentType = new FileContentType("UNKNOWN_FILE");
		assertThat(contentType.getContentType(), is(DEFAULT_CONTENT_TYPE));
	}
}
