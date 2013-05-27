package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class AndRequestMatcher extends CompositeRequestMatcher {
    public AndRequestMatcher(Iterable<RequestMatcher> matchers) {
        super(matchers);
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

    @Override
    public RequestMatcher apply(final MocoConfig config) {
        return new AndRequestMatcher(applyToMatchers(config));
    }
}
