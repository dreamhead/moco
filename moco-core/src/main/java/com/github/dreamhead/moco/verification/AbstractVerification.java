package com.github.dreamhead.moco.verification;

import com.github.dreamhead.moco.VerificationData;
import com.github.dreamhead.moco.VerificationException;
import com.github.dreamhead.moco.VerificationMode;

import static java.lang.String.format;

public abstract class AbstractVerification implements VerificationMode {
    protected abstract boolean meet(int size);

    protected final int count;

    public AbstractVerification(int count) {
        this.count = count;
    }

    @Override
    public void verify(final VerificationData data) {
        int size = data.matchedSize();
        if (!meet(size)) {
            throw new VerificationException(format("expect request hit %d times but %d times", count, size));
        }
    }
}
