package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.runner.monitor.MocoRunnerMonitor;
import com.github.dreamhead.moco.runner.monitor.MonitorFactory;
import com.google.common.base.Optional;

import java.io.File;

import static com.github.dreamhead.moco.runner.FileRunner.createConfigurationFileRunner;
import static com.github.dreamhead.moco.runner.FileRunner.createSettingFileRunner;

public class RunnerFactory {
    private final MonitorFactory monitorFactory = new MonitorFactory();
    private final String shutdownKey;

    public RunnerFactory(String shutdownKey) {
        this.shutdownKey = shutdownKey;
    }

    public ShutdownRunner createRunner(StartArgs startArgs) {
        Runner dynamicRunner = createDynamicRunner(startArgs);
        return createShutdownRunner(dynamicRunner, startArgs.getShutdownPort(), shutdownKey);
    }

    public ShutdownRunner createShutdownRunner(final Runner runner, final Optional<Integer> shutdownPort, final String shutdownKey) {
        return new ShutdownRunner(runner, monitorFactory.createShutdownMonitor(runner, shutdownPort, shutdownKey));
    }

    private Runner createDynamicRunner(StartArgs startArgs) {
        if (startArgs.hasConfigurationFile()) {
            return createDynamicConfigurationRunner(startArgs);
        }

        return createDynamicSettingRunner(startArgs);
    }

    private Runner createDynamicSettingRunner(StartArgs startArgs) {
        final File settingsFile = new File(startArgs.getSettings().get());
        final FileRunner fileRunner = createSettingFileRunner(settingsFile, startArgs);
        final SettingRunner runner = (SettingRunner) fileRunner.getRunner();
        MocoRunnerMonitor fileMocoRunnerMonitor = monitorFactory.createSettingMonitor(settingsFile, runner.getFiles(), fileRunner);
        return new MonitorRunner(fileRunner, fileMocoRunnerMonitor);
    }

    private Runner createDynamicConfigurationRunner(StartArgs startArgs) {
        final File configuration = new File(startArgs.getConfigurationFile().get());
        final FileRunner fileRunner = createConfigurationFileRunner(configuration, startArgs.getPort());
        MocoRunnerMonitor fileMocoRunnerMonitor = monitorFactory.createConfigurationMonitor(configuration, fileRunner);
        return new MonitorRunner(fileRunner, fileMocoRunnerMonitor);
    }

}
