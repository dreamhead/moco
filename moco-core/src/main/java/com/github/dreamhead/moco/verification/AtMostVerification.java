package com.github.dreamhead.moco.verification;

public class AtMostVerification extends AbstractVerification {

    public AtMostVerification(int count) {
        super(count);
    }

    @Override
    protected boolean meet(int size) {
        return size <= count;
    }
}
