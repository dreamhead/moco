package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.runner.monitor.Monitor;
import com.github.dreamhead.moco.runner.monitor.MonitorFactory;

import java.io.File;

import static com.github.dreamhead.moco.runner.FileRunner.createConfigurationFileRunner;
import static com.github.dreamhead.moco.runner.FileRunner.createSettingFileRunner;

public class RunnerFactory {

    private final MonitorFactory monitorFactory = new MonitorFactory();

    private final int defaultShutdownPort;
    private final String defaultShutdownKey;

    public RunnerFactory(int defaultShutdownPort, String defaultShutdownKey) {
        this.defaultShutdownPort = defaultShutdownPort;
        this.defaultShutdownKey = defaultShutdownKey;
    }

    public Runner createRunner(StartArgs startArgs) {
        Runner dynamicRunner = createDynamicRunner(startArgs);
        return monitorFactory.createShutdownMonitor(dynamicRunner, startArgs.getShutdownPort(defaultShutdownPort), defaultShutdownKey);
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
        Monitor fileMonitor = monitorFactory.createSettingMonitor(settingsFile, runner.getFiles(), fileRunner);
        return new MonitorRunner(fileRunner, fileMonitor);
    }

    private Runner createDynamicConfigurationRunner(StartArgs startArgs) {
        final File configuration = new File(startArgs.getConfigurationFile().get());
        final FileRunner fileRunner = createConfigurationFileRunner(configuration, startArgs.getPort());
        Monitor fileMonitor = monitorFactory.createConfigurationMonitor(configuration, fileRunner);
        return new MonitorRunner(fileRunner, fileMonitor);
    }

}
