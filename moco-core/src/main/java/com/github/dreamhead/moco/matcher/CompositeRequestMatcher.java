package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestMatcher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class CompositeRequestMatcher extends AbstractRequestMatcher {
    protected abstract RequestMatcher newMatcher(Iterable<RequestMatcher> matchers);

    private final Iterable<RequestMatcher> matchers;

    protected CompositeRequestMatcher(final Iterable<RequestMatcher> matchers) {
        this.matchers = matchers;
    }

    private Iterable<RequestMatcher> applyToMatchers(final MocoConfig config) {
        List<RequestMatcher> original = StreamSupport.stream(matchers.spliterator(), false).toList();
        List<RequestMatcher> appliedMatchers = original.stream()
                .map(matcher -> matcher.apply(config))
                .toList();
        if (original.equals(appliedMatchers)) {
            return this.matchers;
        }

        return appliedMatchers;
    }


    @Override
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
