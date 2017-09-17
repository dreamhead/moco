package com.github.dreamhead.moco.verification;

public final class AtMostVerification extends UnaryTimesVerification {

    public AtMostVerification(final int count) {
        super(count);
    }

    @Override
    protected boolean doMeet(final int size, final int count) {
        return size <= count;
    }
}
