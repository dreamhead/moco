package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;

public class OrRequestMatcher extends CompositeRequestMatcher {
    public OrRequestMatcher(final Iterable<RequestMatcher> matchers) {
        super(matchers);
    }

    @Override
    public boolean match(final Request request) {
        for (RequestMatcher matcher : matchers) {
            if (matcher.match(request)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected RequestMatcher newMatcher(final Iterable<RequestMatcher> matchers) {
        return new OrRequestMatcher(matchers);
    }
}
