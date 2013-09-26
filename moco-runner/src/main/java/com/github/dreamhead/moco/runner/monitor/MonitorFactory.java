package com.github.dreamhead.moco.runner.monitor;

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

    public ShutdownMocoRunnerMonitor createShutdownMonitor(final Runner runner, Optional<Integer> shutdownPort, String shutdownKey) {
        return new ShutdownMocoRunnerMonitor(shutdownPort, shutdownKey, new ShutdownListener() {
            @Override
            public void onShutdown() {
                runner.stop();
            }
        });
    }

    public FileMocoRunnerMonitor createConfigurationMonitor(final File configuration, final FileRunner fileRunner) {
        return new FileMocoRunnerMonitor(configuration, createListener(fileRunner));
    }

    public MocoRunnerMonitor createSettingMonitor(final File settingsFile, final Iterable<File> configurationFiles, final FileRunner fileRunner) {
        ImmutableList<File> files = ImmutableList.<File>builder().add(settingsFile).addAll(configurationFiles).build();
        return new FilesMocoRunnerMonitor(files, createListener(fileRunner));
    }

    private FileAlterationListenerAdaptor createListener(final FileRunner fileRunner) {
        return new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
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
