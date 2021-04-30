package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;

import java.util.stream.StreamSupport;

public final class OrRequestMatcher extends CompositeRequestMatcher {
    public OrRequestMatcher(final Iterable<RequestMatcher> matchers) {
        super(matchers);
    }

    @Override
    protected boolean doMatch(final Request request, final Iterable<RequestMatcher> matchers) {
        return StreamSupport.stream(matchers.spliterator(), false)
                .anyMatch(requestMatcher -> requestMatcher.match(request));
    }

    @Override
    protected RequestMatcher newMatcher(final Iterable<RequestMatcher> matchers) {
        return new OrRequestMatcher(matchers);
    }
}
