package com.github.dreamhead.moco.monitor;

public final class StdLogWriter implements LogWriter {
    @Override
    public void write(final String content) {
        System.out.println(content);
    }
}
