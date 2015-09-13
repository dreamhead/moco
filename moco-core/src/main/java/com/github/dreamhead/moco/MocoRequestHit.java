package com.github.dreamhead.moco;

import com.github.dreamhead.moco.monitor.DefaultRequestHit;
import com.github.dreamhead.moco.verification.AtLeastVerification;
import com.github.dreamhead.moco.verification.AtMostVerification;
import com.github.dreamhead.moco.verification.BetweenVerification;
import com.github.dreamhead.moco.verification.TimesVerification;

import static com.google.common.base.Preconditions.checkArgument;

public final class MocoRequestHit {
    public static RequestHit requestHit() {
        return new DefaultRequestHit();
    }

    public static UnexpectedRequestMatcher unexpected() {
        return new UnexpectedRequestMatcher();
    }

    public static VerificationMode never() {
        return times(0);
    }

    public static VerificationMode once() {
        return times(1);
    }

    public static VerificationMode times(final int count) {
        checkArgument(count >= 0, "Times count must not be less than zero");
        return new TimesVerification(count);
    }

    public static VerificationMode atLeast(final int count) {
        checkArgument(count > 0, "Times count must be greater than zero");
        return new AtLeastVerification(count);
    }

    public static VerificationMode atMost(final int count) {
        checkArgument(count > 0, "Times count must be greater than zero");
        return new AtMostVerification(count);
    }

    public static VerificationMode between(final int min, final int max) {
        checkArgument(min >= 0, "Min should be greater than or equal to 0");
        checkArgument(max > min, "Max should be greater than min");
        return new BetweenVerification(min, max);
    }

    private MocoRequestHit() {
    }
}
