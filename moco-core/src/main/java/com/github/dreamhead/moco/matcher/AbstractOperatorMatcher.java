package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Lists.newArrayList;

public abstract class AbstractOperatorMatcher<T> implements RequestMatcher {
    protected abstract RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource);

    private final RequestExtractor<T> extractor;
    private final Resource expected;
    private final Predicate<String> predicate;

    protected AbstractOperatorMatcher(final RequestExtractor<T> extractor, final Resource expected, final Predicate<String> predicate) {
        this.extractor = extractor;
        this.predicate = predicate;
        this.expected = expected;
    }

    @Override
    public boolean match(final HttpRequest request) {
        Optional<T> extractContent = extractor.extract(request);
        if (!extractContent.isPresent()) {
            return false;
        }

        T target = extractContent.get();
        if (target instanceof String) {
            return predicate.apply((String)target);
        }

        if (target instanceof String[]) {
            String[] contents = (String[])target;
            return any(newArrayList(contents), predicate);
        }

        return false;
    }

    @Override
    public RequestMatcher apply(final MocoConfig config) {
        Resource appliedResource = expected.apply(config);
        if (appliedResource == expected) {
            return this;
        }

        return newMatcher(extractor, appliedResource);
    }
}