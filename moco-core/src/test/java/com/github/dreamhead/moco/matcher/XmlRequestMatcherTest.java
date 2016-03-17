package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpProtocolVersion;
import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.extractor.ContentRequestExtractor;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static com.github.dreamhead.moco.Moco.text;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class XmlRequestMatcherTest {
    @Test
    public void should_return_false_for_empty_content() {
        XmlRequestMatcher unitUnderTest = new XmlRequestMatcher(new ContentRequestExtractor(),
                text("<request><parameters><id>1</id></parameters></request>"));
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
        assertThat(unitUnderTest.match(request), is(false));
    }

}