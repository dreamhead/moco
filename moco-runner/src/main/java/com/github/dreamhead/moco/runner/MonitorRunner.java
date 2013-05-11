package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.runner.monitor.Monitor;

public class MonitorRunner implements Runner {
    private Runner runner;
    private Monitor monitor;

    public MonitorRunner(Runner runner, Monitor monitor) {
        this.runner = runner;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        this.runner.run();
        this.monitor.startMonitor();
    }

    @Override
    public void stop() {
        this.monitor.stopMonitor();
        this.runner.stop();
    }
}
