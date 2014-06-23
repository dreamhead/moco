package com.github.dreamhead.moco.verification;

public class AtLeastVerification extends UnaryTimesVerification {
    public AtLeastVerification(final int count) {
        super(count);
    }

    @Override
    protected boolean meet(final int size) {
        return size >= count;
    }
}
