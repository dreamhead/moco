package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;

public final class AndRequestMatcher extends CompositeRequestMatcher {
    public AndRequestMatcher(final Iterable<RequestMatcher> matchers) {
        super(matchers);
    }

    @Override
    protected boolean doMatch(final Request request, final Iterable<RequestMatcher> matchers) {
        for (RequestMatcher matcher : matchers) {
            if (!matcher.match(request)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected RequestMatcher newMatcher(final Iterable<RequestMatcher> matchers) {
        return new AndRequestMatcher(matchers);
    }
}
