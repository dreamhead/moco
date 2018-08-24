package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;

public final class NotRequestMatcher extends AbstractRequestMatcher {
    private final RequestMatcher matcher;

    public NotRequestMatcher(final RequestMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(final Request request) {
        return !matcher.match(request);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RequestMatcher doApply(final MocoConfig config) {
        RequestMatcher appliedMatcher = matcher.apply(config);
        if (appliedMatcher == this.matcher) {
            return this;
        }

        return new NotRequestMatcher(appliedMatcher);
    }
}
