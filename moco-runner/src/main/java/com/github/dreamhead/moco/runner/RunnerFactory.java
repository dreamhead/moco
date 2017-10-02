package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.runner.watcher.ShutdownMocoRunnerWatcher;
import com.github.dreamhead.moco.runner.watcher.Watcher;
import com.github.dreamhead.moco.runner.watcher.WatcherFactory;

import java.io.File;

import static com.github.dreamhead.moco.runner.FileRunner.createConfigurationFileRunner;
import static com.github.dreamhead.moco.runner.FileRunner.createSettingFileRunner;
import static com.google.common.collect.ImmutableList.of;

public final class RunnerFactory {
    private final WatcherFactory factory = new WatcherFactory();
    private final String shutdownKey;

    public RunnerFactory(final String shutdownKey) {
        this.shutdownKey = shutdownKey;
    }

    public ShutdownRunner createRunner(final StartArgs startArgs) {
        Runner dynamicRunner = createDynamicRunner(startArgs);
        ShutdownMocoRunnerWatcher watcher = factory.createShutdownWatcher(dynamicRunner, startArgs.getShutdownPort(), shutdownKey);
        return new ShutdownRunner(dynamicRunner, watcher);
    }

    private Runner createDynamicRunner(final StartArgs startArgs) {
        if (startArgs.hasConfigurationFile()) {
            return createDynamicConfigurationRunner(startArgs);
        }

        return createDynamicSettingRunner(startArgs);
    }

    private Runner createDynamicSettingRunner(final StartArgs startArgs) {
        final File settingsFile = new File(startArgs.getSettings().get());
        final FileRunner fileRunner = createSettingFileRunner(settingsFile, startArgs);
        final SettingRunner runner = (SettingRunner) fileRunner.getRunner();
        Watcher watcher = factory.createSettingWatcher(settingsFile,
                runner.getFiles(), fileRunner);
        return new WatcherRunner(fileRunner, watcher);
    }

    private Runner createDynamicConfigurationRunner(final StartArgs startArgs) {
        final File configuration = new File(startArgs.getConfigurationFile().get());
        final FileRunner fileRunner = createConfigurationFileRunner(of(configuration), startArgs);
        Watcher watcher = factory.createConfigurationWatcher(configuration, fileRunner);
        return new WatcherRunner(fileRunner, watcher);
    }
}
