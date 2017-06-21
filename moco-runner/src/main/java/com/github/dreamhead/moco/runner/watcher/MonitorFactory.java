package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.runner.FileRunner;
import com.github.dreamhead.moco.runner.Runner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.io.File;

public class MonitorFactory {
    private WatcherFactory factory = new CommonsIoWatcherFactory();

    public ShutdownMocoRunnerWatcher createShutdownWatcher(final Runner runner,
                                                           final Optional<Integer> shutdownPort,
                                                           final String shutdownKey) {
        return new ShutdownMocoRunnerWatcher(shutdownPort, shutdownKey, new ShutdownListener() {
            @Override
            public void onShutdown() {
                runner.stop();
            }
        });
    }

    public RunnerWatcher createConfigurationWatcher(final File file, final FileRunner fileRunner) {
        return factory.createWatcher(fileRunner, file);
    }

    public RunnerWatcher createSettingWatcher(final File settingsFile,
                                              final Iterable<File> configurationFiles,
                                              final FileRunner fileRunner) {
        ImmutableList<File> files = ImmutableList.<File>builder().add(settingsFile).addAll(configurationFiles).build();
        return factory.createWatcher(fileRunner, files.toArray(new File[files.size()]));
    }
}
