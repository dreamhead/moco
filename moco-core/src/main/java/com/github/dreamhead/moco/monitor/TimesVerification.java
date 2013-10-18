package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.VerificationMode;

public class TimesVerification implements VerificationMode {
    private final int count;

    public TimesVerification(int count) {
        this.count = count;
    }
}
