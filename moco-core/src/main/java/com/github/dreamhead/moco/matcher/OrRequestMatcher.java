package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;

public class OrRequestMatcher extends CompositeRequestMatcher {
    public OrRequestMatcher(Iterable<RequestMatcher> matchers) {
        super(matchers);
    }

    @Override
    public boolean match(final HttpRequest request) {
        for (RequestMatcher matcher : matchers) {
            if (matcher.match(request)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public RequestMatcher apply(final MocoConfig config) {
        Iterable<RequestMatcher> appliedMatchers = applyToMatchers(config);
        if (appliedMatchers == this.matchers) {
            return this;
        }

        return new OrRequestMatcher(applyToMatchers(config));
    }
}
