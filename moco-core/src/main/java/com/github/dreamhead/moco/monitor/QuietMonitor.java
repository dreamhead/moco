package com.github.dreamhead.moco.monitor;

public class QuietMonitor extends AbstractMonitor {
    @Override
    public void onException(final Throwable t) {
        t.printStackTrace(System.err);
    }
}
