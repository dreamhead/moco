package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Predicate;

import java.util.Arrays;

public class EqRequestMatcher<T> extends AbstractOperatorMatcher<T> {
    public EqRequestMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input != null && Arrays.equals(input.getBytes(), expected.readFor(null));
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(RequestExtractor<T> extractor, Resource resource) {
        return new EqRequestMatcher<T>(extractor, resource);
    }
}
