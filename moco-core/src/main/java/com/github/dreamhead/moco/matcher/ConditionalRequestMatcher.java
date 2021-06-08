package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;

import java.util.function.Predicate;

public final class ConditionalRequestMatcher implements RequestMatcher {
    private final Predicate<Request> predicate;

    public ConditionalRequestMatcher(final Predicate<Request> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean match(final Request request) {
        return predicate.test(request);
    }

    @Override
    public RequestMatcher apply(final MocoConfig config) {
        return this;
    }
}
