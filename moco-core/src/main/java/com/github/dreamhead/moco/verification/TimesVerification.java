package com.github.dreamhead.moco.verification;

public class TimesVerification extends AbstractVerification {
    public TimesVerification(int count) {
        super(count);
    }

    protected boolean meet(int size) {
        return size == count;
    }

}
