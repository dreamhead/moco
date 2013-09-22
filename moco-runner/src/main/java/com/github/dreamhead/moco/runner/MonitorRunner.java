package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.runner.monitor.MocoRunnerMonitor;

public class MonitorRunner implements Runner {
    private final Runner runner;
    private final MocoRunnerMonitor mocoRunnerMonitor;

    public MonitorRunner(Runner runner, MocoRunnerMonitor mocoRunnerMonitor) {
        this.runner = runner;
        this.mocoRunnerMonitor = mocoRunnerMonitor;
    }

    @Override
    public void run() {
        this.runner.run();
        this.mocoRunnerMonitor.startMonitor();
    }

    @Override
    public void stop() {
        this.mocoRunnerMonitor.stopMonitor();
        this.runner.stop();
    }
}
