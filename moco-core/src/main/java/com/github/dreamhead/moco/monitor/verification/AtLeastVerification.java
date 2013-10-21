package com.github.dreamhead.moco.monitor.verification;

public class AtLeastVerification extends AbstractVerification {
    public AtLeastVerification(int count) {
        super(count);
    }

    @Override
    protected boolean meet(int size) {
        return size >= count;
    }
}
