package com.github.dreamhead.moco;

import com.google.common.base.Predicate;

import static com.google.common.collect.FluentIterable.from;
import static java.lang.String.format;

public final class VerificationData {
    private final Iterable<Request> requests;
    private final RequestMatcher matcher;
    private final String mismatchFormat;

    public VerificationData(final Iterable<Request> requests,
                            final RequestMatcher matcher,
                            final String mismatchFormat) {
        this.requests = requests;
        this.matcher = matcher;
        this.mismatchFormat = mismatchFormat;
    }

    public String mismatchDescription(final int actualSize, final String expected) {
        return format(mismatchFormat, expected, actualSize);
    }

    public int matchedSize() {
        return from(requests).filter(matched()).size();
    }

    private Predicate<Request> matched() {
        return new Predicate<Request>() {
            @Override
            public boolean apply(final Request request) {
                return matcher.match(request);
            }
        };
    }
}
