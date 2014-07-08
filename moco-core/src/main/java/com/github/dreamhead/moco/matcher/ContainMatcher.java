package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

public class ContainMatcher<T> extends AbstractOperatorMatcher<T> {
    public ContainMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input.contains(new String(expected.readFor(Optional.<Request>absent())));
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new ContainMatcher<T>(extractor, resource);
    }
}
