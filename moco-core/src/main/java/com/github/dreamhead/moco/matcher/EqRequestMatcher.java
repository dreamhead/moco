package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.Arrays;

public class EqRequestMatcher<T> extends AbstractOperatorMatcher<T> {
    public EqRequestMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return Arrays.equals(input.getBytes(), expected.readFor(Optional.<Request>absent()));
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new EqRequestMatcher<T>(extractor, resource);
    }
}
