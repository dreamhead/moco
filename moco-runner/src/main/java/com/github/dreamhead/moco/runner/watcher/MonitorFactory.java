package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.runner.FileRunner;
import com.github.dreamhead.moco.runner.Runner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MonitorFactory {
    private static Logger logger = LoggerFactory.getLogger(MonitorFactory.class);

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

    public FileMocoRunnerWatcher createConfigurationWatcher(final File configuration, final FileRunner fileRunner) {
        return new FileMocoRunnerWatcher(configuration, createListener(fileRunner));
    }

    public MocoRunnerWatcher createSettingWatcher(final File settingsFile,
                                                  final Iterable<File> configurationFiles,
                                                  final FileRunner fileRunner) {
        ImmutableList<File> files = ImmutableList.<File>builder().add(settingsFile).addAll(configurationFiles).build();
        return new FilesMocoRunnerWatcher(files, createListener(fileRunner));
    }

    private FileAlterationListenerAdaptor createListener(final FileRunner fileRunner) {
        return new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(final File file) {
                logger.info("{} change detected.", file.getName());
                try {
                    fileRunner.restart();
                } catch (Exception e) {
                    logger.error("Fail to load configuration in {}.", file.getName());
                    logger.error(e.getMessage());
                }
            }
        };
    }
}
