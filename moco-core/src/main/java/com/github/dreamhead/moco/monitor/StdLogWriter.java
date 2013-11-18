package com.github.dreamhead.moco.monitor;

public class StdLogWriter implements LogWriter {
    @Override
    public void write(String content) {
        System.out.println(content);
    }
}
