package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Predicate;

import java.util.regex.Pattern;

public class MatchMatcher<T> extends AbstractOperatorMatcher<T> {
    private final Resource expected;

    public MatchMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, new Predicate<String>() {

            @Override
            public boolean apply(String input) {
                Pattern pattern = Pattern.compile(new String(expected.readFor(null)));
                return pattern.matcher(input).matches();
            }
        });
        this.expected = expected;
    }

    @Override
    public RequestMatcher apply(final MocoConfig config) {
        Resource appliedResource = expected.apply(config);
        if (appliedResource == expected) {
            return this;
        }

        return new MatchMatcher<T>(extractor, appliedResource);
    }
}
