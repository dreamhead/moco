package com.github.dreamhead.moco;

public interface RequestHit extends MocoMonitor {
    void verify(UnexpectedRequestMatcher matcher, VerificationMode mode);

    void verify(RequestMatcher matcher, VerificationMode mode);
}
