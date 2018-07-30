package com.github.dreamhead.moco.verification;

public abstract class UnaryTimesVerification extends AbstractTimesVerification {
    protected abstract boolean doMeet(int size, int count);

    private final int count;

    protected UnaryTimesVerification(final int count) {
        this.count = count;
    }

    @Override
    protected final String expectedTip() {
        return Integer.toString(count);
    }

    @Override
    protected final boolean meet(final int size) {
        return doMeet(size, count);
    }
}
