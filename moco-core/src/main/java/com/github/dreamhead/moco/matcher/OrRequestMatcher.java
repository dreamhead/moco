package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class OrRequestMatcher implements RequestMatcher {
    private RequestMatcher[] matchers;

    public OrRequestMatcher(RequestMatcher[] matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean match(HttpRequest request) {
        for (RequestMatcher matcher : matchers) {
            if (matcher.match(request)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void apply(MocoConfig config) {
        for (RequestMatcher matcher : matchers) {
            matcher.apply(config);
        }
    }
}
