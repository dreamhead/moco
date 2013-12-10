package com.github.dreamhead.moco;

public class UnexpectedRequestMatcher implements RequestMatcher {
    @Override
    public boolean match(HttpRequest request) {
        return true;
    }

    @Override
    public RequestMatcher apply(MocoConfig config) {
        return this;
    }
}
