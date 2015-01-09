package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import static com.google.common.collect.FluentIterable.from;

public abstract class CompositeRequestMatcher extends AbstractRequestMatcher {
    protected abstract RequestMatcher newMatcher(Iterable<RequestMatcher> matchers);

    protected final Iterable<RequestMatcher> matchers;

    public CompositeRequestMatcher(final Iterable<RequestMatcher> matchers) {
        this.matchers = matchers;
    }

    protected Iterable<RequestMatcher> applyToMatchers(final MocoConfig config) {
        FluentIterable<RequestMatcher> appliedMatchers = from(matchers).transform(applyConfig(config));
        if (matchers.equals(appliedMatchers)) {
            return this.matchers;
        }

        return appliedMatchers;
    }

    private Function<RequestMatcher, RequestMatcher> applyConfig(final MocoConfig config) {
        return new Function<RequestMatcher, RequestMatcher>() {
            @Override
            public RequestMatcher apply(RequestMatcher matcher) {
                return matcher.apply(config);
            }
        };
    }


    @Override
    @SuppressWarnings("unchecked")
    public RequestMatcher doApply(final MocoConfig config) {
        Iterable<RequestMatcher> appliedMatchers = applyToMatchers(config);
        if (appliedMatchers == this.matchers) {
            return this;
        }

        return newMatcher(appliedMatchers);
    }
}
