package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;

public class NotRequestMatcher implements RequestMatcher {
    private final RequestMatcher matcher;

    public NotRequestMatcher(RequestMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(HttpRequest request) {
        return !matcher.match(request);
    }

    @Override
    public RequestMatcher apply(MocoConfig config) {
        RequestMatcher appliedMatcher = matcher.apply(config);
        if (appliedMatcher == this.matcher) {
            return this;
        }

        return new NotRequestMatcher(appliedMatcher);
    }
}
