package com.github.dreamhead.moco.verification;

import com.github.dreamhead.moco.VerificationData;
import com.github.dreamhead.moco.VerificationException;
import com.github.dreamhead.moco.VerificationMode;

public class BetweenVerification implements VerificationMode {
    private final int min;
    private final int max;

    public BetweenVerification(final int min, final int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void verify(VerificationData data) {
        int actualSize = data.matchedSize();
        if (actualSize < min || actualSize > max) {
            throw new VerificationException(data.mismatchDescription(actualSize, min));
        }
    }
}
