package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InternalApisTest {
    @Test
    public void should_create_context_correctly() {
        RequestMatcher matcher = InternalApis.context("targets");
        assertThat(matcher.match(requestByUri("targets/hello")), is(true));
        assertThat(matcher.match(requestByUri("targets")), is(true));
    }

    @Test
    public void should_not_match_mismatch_uri() {
        RequestMatcher matcher = InternalApis.context("targets");
        assertThat(matcher.match(requestByUri("something")), is(false));
        assertThat(matcher.match(requestByUri("targetshello")), is(false));
    }

    private HttpRequest requestByUri(final String uri) {
        return DefaultHttpRequest.builder().withUri(uri).build();
    }
}
