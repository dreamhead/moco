package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Predicate;

public final class EqRequestMatcher<T> extends AbstractOperatorMatcher<T> {
    public EqRequestMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                return input.equals(expected.readFor(null).toString());
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new EqRequestMatcher<>(extractor, resource);
    }
}
