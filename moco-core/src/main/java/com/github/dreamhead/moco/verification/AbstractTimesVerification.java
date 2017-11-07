package com.github.dreamhead.moco.verification;

import com.github.dreamhead.moco.VerificationData;
import com.github.dreamhead.moco.VerificationException;
import com.github.dreamhead.moco.VerificationMode;

public abstract class AbstractTimesVerification implements VerificationMode {
    protected abstract boolean meet(int size);
    protected abstract String expectedTip();

    @Override
    public final void verify(final VerificationData data) {
        int actualSize = data.matchedSize();
        if (!meet(actualSize)) {
            throw new VerificationException(data.mismatchDescription(actualSize, expectedTip()));
        }
    }
}
