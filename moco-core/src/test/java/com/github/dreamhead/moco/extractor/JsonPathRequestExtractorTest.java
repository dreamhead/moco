package com.github.dreamhead.moco.extractor;

import org.junit.Test;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.collect.ImmutableMap;

public class JsonPathRequestExtractorTest {

	@Test
	public void testDoExtractHttpRequest() {
		JsonPathRequestExtractor unitUnderTest = new JsonPathRequestExtractor("$..account");
		HttpRequest request = new HttpRequest() {
			
			@Override
			public HttpProtocolVersion getVersion() {
				return HttpProtocolVersion.VERSION_1_1;
			}
			
			@Override
			public ImmutableMap<String, String> getHeaders() {
				return ImmutableMap.of();
			}
			
			@Override
			public MessageContent getContent() {
				return MessageContent.content("");
			}
			
			@Override
			public String getUri() {
				return "/";
			}
			
			@Override
			public ImmutableMap<String, String[]> getQueries() {
				return ImmutableMap.of();
			}
			
			@Override
			public HttpMethod getMethod() {
				return HttpMethod.GET;
			}
		};
		unitUnderTest.doExtract(request);
	}

}
