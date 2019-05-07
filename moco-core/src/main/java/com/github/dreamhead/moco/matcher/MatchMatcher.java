package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Predicate;

import java.util.regex.Pattern;

public final class MatchMatcher<T> extends AbstractOperatorMatcher<T> {
    public MatchMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                Pattern pattern = Pattern.compile(expected.readFor(null).toString());
                return pattern.matcher(input).matches();
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new MatchMatcher<>(extractor, resource);
    }
}
