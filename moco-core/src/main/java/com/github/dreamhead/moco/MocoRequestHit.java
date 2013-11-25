package com.github.dreamhead.moco;

import com.github.dreamhead.moco.monitor.DefaultRequestHit;
import com.github.dreamhead.moco.verification.AtLeastVerification;
import com.github.dreamhead.moco.verification.AtMostVerification;
import com.github.dreamhead.moco.verification.TimesVerification;

import static com.google.common.base.Preconditions.checkArgument;

public class MocoRequestHit {
    public static RequestHit requestHit() {
        return new DefaultRequestHit();
    }

    public static UnexpectedRequestMatcher unexpected() {
        return new UnexpectedRequestMatcher();
    }

    public static VerificationMode never() {
        return times(0);
    }

    public static VerificationMode times(int count) {
        checkArgument(count >= 0, "Times count must not be less than zero");
        return new TimesVerification(count);
    }

    public static VerificationMode atLeast(int count) {
        checkArgument(count > 0, "Times count must be greater than zero");
        return new AtLeastVerification(count);
    }

    public static VerificationMode atMost(int count) {
        checkArgument(count > 0, "Times count must be greater than zero");
        return new AtMostVerification(count);
    }

    private MocoRequestHit() {}
}
