package com.github.dreamhead.moco.matcher;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.regex.Pattern;

public class MatchMatcher<T> extends AbstractOperatorMatcher<T> {
    public MatchMatcher(final RequestExtractor<T> extractor, final Resource expected) {
        super(extractor, expected, new Predicate<byte[]>() {
            @Override
            public boolean apply(final byte[] input) {
                Pattern pattern = Pattern.compile(expected.readFor(Optional.<Request>absent()).toString());
                return pattern.matcher(new String(input)).matches();
            }
        });
    }

    @Override
    protected RequestMatcher newMatcher(final RequestExtractor<T> extractor, final Resource resource) {
        return new MatchMatcher<T>(extractor, resource);
    }
}
