package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.base.Optional;

public final class ExistMatcher<T> extends AbstractRequestMatcher {
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
    public RequestMatcher doApply(final MocoConfig config) {
        return this;
    }
}
