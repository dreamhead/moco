package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

public class ContainMatcher<T> extends AbstractOperatorMatcher<T> {
    public ContainMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<byte[]>() {
            @Override
            public boolean apply(final byte[] input) {
                return new String(input).contains(expected.readFor(Optional.<Request>absent()).toString());
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new ContainMatcher<T>(extractor, resource);
    }
}
