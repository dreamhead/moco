package com.github.dreamhead.moco;

import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.http.FullHttpRequest;

public class VerificationData {
    private final ImmutableList<FullHttpRequest> requests;
    private final RequestMatcher matcher;

    public VerificationData(ImmutableList<FullHttpRequest> requests, RequestMatcher matcher) {
        this.requests = requests;
        this.matcher = matcher;
    }

    public ImmutableList<FullHttpRequest> getRequests() {
        return requests;
    }

    public RequestMatcher getMatcher() {
        return matcher;
    }
}
