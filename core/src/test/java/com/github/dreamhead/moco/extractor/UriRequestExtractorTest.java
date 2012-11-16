package com.github.dreamhead.moco.extractor;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UriRequestExtractorTest {
    private UriRequestExtractor extractor;
    private HttpRequest request;

    @Before
    public void setUp() throws Exception {
        extractor = new UriRequestExtractor();
        request = mock(HttpRequest.class);
    }

    @Test
    public void should_return_all_uri_while_uri_has_no_parameter() {
        when(request.getUri()).thenReturn("/foo");
        assertThat(extractor.extract(request), is("/foo"));
    }

    @Test
    public void should_return_path_part_while_uri_has_parameters() {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getUri()).thenReturn("/foo?param=bar");
        assertThat(extractor.extract(request), is("/foo"));
    }
}
