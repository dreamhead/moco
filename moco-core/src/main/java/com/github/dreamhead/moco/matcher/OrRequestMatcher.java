package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;

public class OrRequestMatcher extends CompositeRequestMatcher {
    public OrRequestMatcher(Iterable<RequestMatcher> matchers) {
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
    protected RequestMatcher newMatcher(Iterable<RequestMatcher> matchers) {
        return new OrRequestMatcher(matchers);
    }
}
