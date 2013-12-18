package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EqRequestMatcherTest {
    private EqRequestMatcher matcher;
    private RequestExtractor<String[]> extractor;
    private HttpRequest request;
    private Resource expected;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        extractor = (RequestExtractor<String[]>)mock(RequestExtractor.class);
        request = mock(HttpRequest.class);
        expected = mock(Resource.class);
        matcher = new EqRequestMatcher(extractor, expected);
    }

    @Test
    public void should_return_false_when_extracted_string_array_have_null() {
        Optional<String[]> extractedResultsWithNull = Optional.of(new String[]{null, null});
        when(extractor.extract(request)).thenReturn(extractedResultsWithNull);
        assertThat(matcher.match(request), is(false));
    }
}
