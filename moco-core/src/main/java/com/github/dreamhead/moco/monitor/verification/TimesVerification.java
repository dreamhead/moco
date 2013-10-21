package com.github.dreamhead.moco.monitor.verification;

public class TimesVerification extends AbstractVerification {
    public TimesVerification(int count) {
        super(count);
    }

    protected boolean meet(int size) {
        return size == count;
    }

}
