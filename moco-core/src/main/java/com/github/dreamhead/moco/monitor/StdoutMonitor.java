package com.github.dreamhead.moco.monitor;

public class StdoutMonitor extends OutputMonitor {
    @Override
    protected void log(String content) {
        System.out.println(content);
    }
}
