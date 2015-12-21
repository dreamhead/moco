package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

public class EndsWithMatcher<T> extends AbstractOperatorMatcher<T> {
    public EndsWithMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<byte[]>() {
            @Override
            public boolean apply(final byte[] input) {
                return new String(input).endsWith(expected.readFor(Optional.<Request>absent()).toString());
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new StartsWithMatcher<T>(extractor, resource);
    }
}
