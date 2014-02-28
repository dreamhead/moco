package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Predicate;

public class EndsWithMatcher<T> extends AbstractOperatorMatcher<T> {
    private Resource expected;

    public EndsWithMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input.endsWith(new String(expected.readFor(null)));
            }
        });
        this.expected = expected;
    }

    @Override
    public RequestMatcher apply(MocoConfig config) {
        Resource appliedResource = expected.apply(config);
        if (appliedResource == expected) {
            return this;
        }

        return new StartsWithMatcher<T>(extractor, appliedResource);
    }
}
