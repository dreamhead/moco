package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XPathRequestExtractorTest {
    @Test
    public void should_extract_empty_content_as_absent() {
        XPathRequestExtractor unitUnderTest = new XPathRequestExtractor("/request/parameters/id/text()");
        HttpRequest request = DefaultHttpRequest.builder().withStringContent("").build();
        Optional<String[]> result = unitUnderTest.extract(request);
        assertThat(result.isPresent(), is(false));
    }
}
