package com.github.dreamhead.moco.extractor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import org.junit.Before;
import org.junit.Test;

public class VersionExtractorTest {
	private VersionExtractor extractor;
	private FullHttpRequest request;

	@Before
	public void setUp() {
		extractor = new VersionExtractor();
	}

	@Test
	public void shoud_get_right_version_with_HTTP_1_0() {
		request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0,
				HttpMethod.GET, "/foo");
		assertThat(extractor.extract(request),
				is(HttpVersion.HTTP_1_0.toString()));
	}

	@Test
	public void shoud_get_right_version_with_HTTP_1_1() {
		request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
				HttpMethod.GET, "/foo");
		assertThat(extractor.extract(request),
				is(HttpVersion.HTTP_1_1.toString()));
	}
}
