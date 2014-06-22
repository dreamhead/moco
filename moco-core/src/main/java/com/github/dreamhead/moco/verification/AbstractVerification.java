package com.github.dreamhead.moco.verification;

import com.github.dreamhead.moco.VerificationData;
import com.github.dreamhead.moco.VerificationException;
import com.github.dreamhead.moco.VerificationMode;

public abstract class AbstractVerification implements VerificationMode {
    protected abstract boolean meet(int size);

    protected final int count;

    public AbstractVerification(int count) {
        this.count = count;
    }

    @Override
    public void verify(final VerificationData data) {
        int actualSize = data.matchedSize();
        if (!meet(actualSize)) {
            throw new VerificationException(data.mismatchDescription(actualSize, Integer.toString(count)));
        }
    }
}
