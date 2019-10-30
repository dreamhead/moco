package com.github.dreamhead.moco;

import java.util.stream.StreamSupport;

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
        return (int) StreamSupport.stream(requests.spliterator(), false)
                .filter(matcher::match)
                .count();
    }
}
