package com.github.dreamhead.moco.extractor;

import com.github.dreamhead.moco.HttpMethod;
import com.github.dreamhead.moco.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpMethodExtractorTest {
    private HttpMethodExtractor extractor;
    private HttpRequest request;

    @BeforeEach
    public void setUp() {
        extractor = new HttpMethodExtractor();
        request = mock(HttpRequest.class);
    }

    @Test
    public void should_extract_http_method() {
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        assertThat(extractor.extract(request).get(), is(HttpMethod.GET.name()));
    }
}
