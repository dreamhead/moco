package com.github.dreamhead.moco;

import io.netty.handler.codec.http.FullHttpRequest;

public class UnexpectedRequestMatcher implements RequestMatcher {
    @Override
    public boolean match(FullHttpRequest request) {
        return true;
    }

    @Override
    public RequestMatcher apply(MocoConfig config) {
        return this;
    }
}
