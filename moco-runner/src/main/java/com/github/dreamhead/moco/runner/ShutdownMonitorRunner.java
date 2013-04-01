package com.github.dreamhead.moco.runner;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;

public class ShutdownMonitorRunner implements Runner {
    private Runner runner;
    private String shutdownHookFile;
    private FileMonitor monitor = new FileMonitor();

    public ShutdownMonitorRunner(Runner runner, String shutdownHookFile) {
        this.runner = runner;
        this.shutdownHookFile = shutdownHookFile;
    }

    @Override
    public void run() {
        runner.run();

        monitor.startMonitor(new File(shutdownHookFile), new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                file.delete();
                stop();
            }
        });
    }

    @Override
    public void stop() {
        runner.stop();
        monitor.stopMonitor();
    }
}
