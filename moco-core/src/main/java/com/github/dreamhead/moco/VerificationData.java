package com.github.dreamhead.moco;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.FullHttpRequest;

public class VerificationData {
    private final ImmutableList<FullHttpRequest> requests;
    private final RequestMatcher matcher;

    public VerificationData(ImmutableList<FullHttpRequest> requests, RequestMatcher matcher) {
        this.requests = requests;
        this.matcher = matcher;
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
