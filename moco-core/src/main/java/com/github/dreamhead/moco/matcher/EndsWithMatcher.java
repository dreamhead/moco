package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;

public final class EndsWithMatcher<T> extends AbstractOperatorMatcher<T> {
    public EndsWithMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, input -> input.endsWith(expected.readFor(null).toString()));
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new StartsWithMatcher<>(extractor, resource);
    }
}
