package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Predicate;

public final class EndsWithMatcher<T> extends AbstractOperatorMatcher<T> {
    public EndsWithMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                return input.endsWith(expected.readFor(null).toString());
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new StartsWithMatcher<>(extractor, resource);
    }
}
