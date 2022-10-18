package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;

public class StartsWithMatcher<T> extends AbstractOperatorMatcher<T> {
    public StartsWithMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, input -> input.startsWith(expected.readFor((Request) null).toString()));
    }

    @Override
    protected final RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new StartsWithMatcher<>(extractor, resource);
    }
}
