package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Predicate;

public final class ContainMatcher<T> extends AbstractOperatorMatcher<T> {
    public ContainMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                return input.contains(expected.readFor(null).toString());
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new ContainMatcher<>(extractor, resource);
    }
}
