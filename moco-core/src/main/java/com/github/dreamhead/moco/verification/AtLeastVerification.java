package com.github.dreamhead.moco.verification;

public final class AtLeastVerification extends UnaryTimesVerification {
    public AtLeastVerification(final int count) {
        super(count);
    }

    @Override
    protected boolean doMeet(final int size, final int count) {
        return size >= count;
    }
}
