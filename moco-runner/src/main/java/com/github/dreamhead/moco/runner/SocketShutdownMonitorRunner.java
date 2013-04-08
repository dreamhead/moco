package com.github.dreamhead.moco.runner;

public class SocketShutdownMonitorRunner implements Runner {
    private final Runner runner;
    private final ShutdownMonitor monitor;

    public SocketShutdownMonitorRunner(final Runner runner, int shutdownPort, String shutdownKey) {
        this.runner = runner;
        this.monitor = new ShutdownMonitor(shutdownPort, shutdownKey, new ShutdownListener() {
            @Override
            public void onShutdown() {
                runner.stop();
            }
        });
    }

    @Override
    public void run() {
        this.runner.run();
        this.monitor.startMonitor();
    }

    @Override
    public void stop() {
        this.runner.stop();
        this.monitor.stopMonitor();
    }
}
