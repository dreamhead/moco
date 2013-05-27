package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.base.Function;

import static com.google.common.collect.FluentIterable.from;

public abstract class CompositeRequestMatcher implements RequestMatcher {
    protected Iterable<RequestMatcher> matchers;

    public CompositeRequestMatcher(Iterable<RequestMatcher> matchers) {
        this.matchers = matchers;
    }

    protected Iterable<RequestMatcher> applyToMatchers(MocoConfig config) {
        return from(matchers).transform(applyConfig(config));
    }

    private Function<RequestMatcher, RequestMatcher> applyConfig(final MocoConfig config) {
        return new Function<RequestMatcher, RequestMatcher>() {
            @Override
            public RequestMatcher apply(RequestMatcher matcher) {
                return matcher.apply(config);
            }
        };
    }
}
