package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.arg.StartArgs;
import com.github.dreamhead.moco.runner.watcher.ShutdownMocoRunnerWatcher;
import com.github.dreamhead.moco.runner.watcher.Watcher;
import com.github.dreamhead.moco.runner.watcher.WatcherFactory;
import com.google.common.collect.ImmutableList;

import java.io.File;

import static com.github.dreamhead.moco.runner.FileRunner.createConfigurationFileRunner;
import static com.github.dreamhead.moco.runner.FileRunner.createSettingFileRunner;
import static com.github.dreamhead.moco.util.Files.filenameToFile;
import static com.github.dreamhead.moco.util.Globs.glob;
import static com.google.common.collect.FluentIterable.from;

public final class RunnerFactory {
    private final WatcherFactory factory = new WatcherFactory();
    private final String shutdownKey;

    public RunnerFactory(final String shutdownKey) {
        this.shutdownKey = shutdownKey;
    }

    public ShutdownRunner createRunner(final StartArgs startArgs) {
        Runner dynamicRunner = createDynamicRunner(startArgs);
        ShutdownMocoRunnerWatcher watcher = factory.createShutdownWatcher(dynamicRunner,
                startArgs.getShutdownPort().or(0), shutdownKey);
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
        String pathname = startArgs.getConfigurationFile().get();
        ImmutableList<String> glob = glob(pathname);
        Iterable<File> files = from(glob).transform(filenameToFile());
        final FileRunner fileRunner = createConfigurationFileRunner(files, startArgs);
        Watcher watcher = factory.createConfigurationWatcher(files, fileRunner);
        return new WatcherRunner(fileRunner, watcher);
    }
}
