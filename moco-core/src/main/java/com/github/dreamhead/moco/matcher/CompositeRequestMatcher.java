package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import static com.google.common.collect.FluentIterable.from;

public abstract class CompositeRequestMatcher extends AbstractRequestMatcher {
    protected abstract RequestMatcher newMatcher(Iterable<RequestMatcher> matchers);

    private final Iterable<RequestMatcher> matchers;

    protected CompositeRequestMatcher(final Iterable<RequestMatcher> matchers) {
        this.matchers = matchers;
    }

    private Iterable<RequestMatcher> applyToMatchers(final MocoConfig config) {
        FluentIterable<RequestMatcher> appliedMatchers = from(matchers).transform(applyConfig(config));
        if (matchers.equals(appliedMatchers)) {
            return this.matchers;
        }

        return appliedMatchers;
    }

    private Function<RequestMatcher, RequestMatcher> applyConfig(final MocoConfig config) {
        return new Function<RequestMatcher, RequestMatcher>() {
            @Override
            public RequestMatcher apply(final RequestMatcher matcher) {
                return matcher.apply(config);
            }
        };
    }


    @Override
    @SuppressWarnings("unchecked")
    public final RequestMatcher doApply(final MocoConfig config) {
        Iterable<RequestMatcher> appliedMatchers = applyToMatchers(config);
        if (appliedMatchers == this.matchers) {
            return this;
        }

        return newMatcher(appliedMatchers);
    }

    @Override
    public final boolean match(final Request request) {
        return doMatch(request, matchers);
    }

    protected abstract boolean doMatch(Request request, Iterable<RequestMatcher> matchers);
}
