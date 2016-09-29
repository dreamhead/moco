package com.github.dreamhead.moco.verification;

public abstract class UnaryTimesVerification extends AbstractTimesVerification {
    protected abstract boolean doMeet(final int size, final int count);

    private final int count;

    protected UnaryTimesVerification(final int count) {
        this.count = count;
    }

    @Override
    protected String expectedTip() {
        return Integer.toString(count);
    }

    @Override
    protected boolean meet(final int size) {
        return doMeet(size, count);
    }
}
