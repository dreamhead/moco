package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.google.common.base.Optional;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JsonPathRequestExtractorTest {

    @Test
    public void should_extract_empty_content_as_absent() {
        JsonPathRequestExtractor unitUnderTest = new JsonPathRequestExtractor("$..account");
        HttpRequest request = DefaultHttpRequest.builder()
                .withContent("")
                .build();
        Optional<Object> result = unitUnderTest.extract(request);
        assertThat(result.isPresent(), is(false));
    }
}
