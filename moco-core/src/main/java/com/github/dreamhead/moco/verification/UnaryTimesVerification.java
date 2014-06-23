package com.github.dreamhead.moco.verification;

public abstract class UnaryTimesVerification extends AbstractTimesVerification {
    protected final int count;

    public UnaryTimesVerification(final int count) {
        this.count = count;
    }

    @Override
    protected String expectedTip() {
        return Integer.toString(count);
    }
}
