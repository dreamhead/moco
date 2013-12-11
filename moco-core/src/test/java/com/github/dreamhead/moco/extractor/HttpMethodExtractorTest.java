package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpMethodExtractorTest {
	private HttpMethodExtractor extractor;
	private HttpRequest request;

	@Before
	public void setUp() {
		extractor = new HttpMethodExtractor();
        request = mock(HttpRequest.class);
	}

	@Test
	public void should_extract_http_method() {
        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());
		assertThat(extractor.extract(request).get(), is(HttpMethod.GET.toString()));
	}
}
