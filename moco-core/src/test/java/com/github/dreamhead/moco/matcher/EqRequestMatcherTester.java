package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EqRequestMatcherTester {
    private EqRequestMatcher classUnderTest;
    private RequestExtractor extractor;
    private HttpRequest request;
    private Resource expected;

    @Before
    public void setUp() {
        extractor = mock(RequestExtractor.class);
        request = mock(HttpRequest.class);
        expected = mock(Resource.class);

        classUnderTest = new EqRequestMatcher(extractor, expected);
    }

    @After
    public void cleanUp() {
        classUnderTest = null;

        expected = null;
        request = null;
        extractor = null;
    }

    @Test
    public void should_return_false_when_input_is_null() {
        when(request.getMethod()).thenReturn(HttpMethod.POST.toString());
        Optional<String[]> elementAllNullArray = Optional.of(new String[]{null, null});
        when(extractor.extract(request)).thenReturn(elementAllNullArray);

        assertFalse(classUnderTest.match(request));
    }
}
