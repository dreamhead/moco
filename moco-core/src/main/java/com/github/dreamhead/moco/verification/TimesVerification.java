package com.github.dreamhead.moco.verification;

public class TimesVerification extends UnaryTimesVerification {
    public TimesVerification(final int count) {
        super(count);
    }

    protected boolean meet(final int size) {
        return size == count;
    }
}
