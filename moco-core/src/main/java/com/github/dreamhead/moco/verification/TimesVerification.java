package com.github.dreamhead.moco.verification;

public final class TimesVerification extends UnaryTimesVerification {
    public TimesVerification(final int count) {
        super(count);
    }

    @Override
    protected boolean doMeet(final int size, final int count) {
        return size == count;
    }
}
