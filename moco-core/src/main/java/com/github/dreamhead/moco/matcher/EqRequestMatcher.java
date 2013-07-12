package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Predicate;

import java.util.Arrays;

public class EqRequestMatcher<T> extends AbstractOperatorMatcher<T> {
    private final Resource expected;

    public EqRequestMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return Arrays.equals(input.getBytes(), expected.readFor(null));
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

        return new EqRequestMatcher<T>(extractor, appliedResource);
    }
}
