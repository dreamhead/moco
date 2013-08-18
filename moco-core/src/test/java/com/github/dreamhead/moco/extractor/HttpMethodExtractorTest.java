package com.github.dreamhead.moco.extractor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import org.junit.Before;
import org.junit.Test;

public class HttpMethodExtractorTest {
	private HttpMethodExtractor extractor;
	private FullHttpRequest request;

	@Before
	public void setUp() {
		extractor = new HttpMethodExtractor();
	}

	@Test
	public void should_return_get_with_get_method() {
		request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/foo");
		assertThat(extractor.extract(request), is(HttpMethod.GET.toString()));
	}

	@Test
	public void should_return_post_with_post_method() {
		request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, "/foo");
		assertThat(extractor.extract(request), is(HttpMethod.POST.toString()));
	}
	
	@Test
	public void should_return_delete_with_delete_method() {
		request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.DELETE, "/foo");
		assertThat(extractor.extract(request), is(HttpMethod.DELETE.toString()));
	}
}
