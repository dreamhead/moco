package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class XPathRequestExtractorTest {
    @Test
    public void should_extract_empty_content_as_absent() {
        XPathRequestExtractor unitUnderTest = new XPathRequestExtractor("/request/parameters/id/text()");
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
        Optional<String[]> result = unitUnderTest.extract(request);
        assertThat(result.isPresent(), is(false));
    }

}