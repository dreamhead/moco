package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.base.Optional;

public class ExistMatcher<T> implements RequestMatcher {
    private final RequestExtractor<T> extractor;

    public ExistMatcher(final RequestExtractor<T> extractor) {
        this.extractor = extractor;
    }

    @Override
    public boolean match(final Request request) {
        Optional<T> extractContent = extractor.extract(request);
        return extractContent.isPresent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RequestMatcher apply(final MocoConfig config) {
        if (config.isFor(MocoConfig.REQUEST_ID)) {
            return (RequestMatcher)config.apply(this);
        }

        return this;
    }
}
