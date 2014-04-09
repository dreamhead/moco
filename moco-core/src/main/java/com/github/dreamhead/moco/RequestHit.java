package com.github.dreamhead.moco;

public interface RequestHit extends MocoMonitor {
    void verify(final UnexpectedRequestMatcher matcher, final VerificationMode mode);

    void verify(final RequestMatcher matcher, final VerificationMode mode);
}
