package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;

public final class ContainMatcher<T> extends AbstractOperatorMatcher<T> {
    public ContainMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, input -> input.contains(expected.readFor((Request) null).toString()));
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new ContainMatcher<>(extractor, resource);
    }
}
