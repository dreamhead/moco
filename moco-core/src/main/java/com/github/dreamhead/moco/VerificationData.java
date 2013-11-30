package com.github.dreamhead.moco;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.FullHttpRequest;

import static java.lang.String.format;

public class VerificationData {
    private final ImmutableList<FullHttpRequest> requests;
    private final RequestMatcher matcher;
    private final String mismatchFormat;

    public VerificationData(ImmutableList<FullHttpRequest> requests, RequestMatcher matcher, String mismatchFormat) {
        this.requests = requests;
        this.matcher = matcher;
        this.mismatchFormat = mismatchFormat;
    }

    public String mismatchDescription(int actualSize, int expectedCount) {
        return format(mismatchFormat, expectedCount, actualSize);
    }

    public int matchedSize() {
        return FluentIterable.from(requests).filter(matched()).size();
    }

    private Predicate<FullHttpRequest> matched() {
        return new Predicate<FullHttpRequest>() {
            @Override
            public boolean apply(FullHttpRequest request) {
                return matcher.match(request);
            }
        };
    }
}
