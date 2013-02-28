package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class AndRequestMatcher implements RequestMatcher {
    private Iterable<RequestMatcher> matchers;

    public AndRequestMatcher(Iterable<RequestMatcher> matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean match(HttpRequest request) {
        for (RequestMatcher matcher : matchers) {
            if (!matcher.match(request)) {
                return false;
            }
        }

        return true;
    }
}
