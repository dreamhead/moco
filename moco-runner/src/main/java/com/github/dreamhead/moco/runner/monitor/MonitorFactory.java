package com.github.dreamhead.moco.runner.monitor;

import com.github.dreamhead.moco.runner.*;
import com.google.common.collect.Iterables;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class MonitorFactory {
    private static Logger logger = LoggerFactory.getLogger(MonitorFactory.class);

    public Runner createShutdownMonitor(final Runner runner, final int shutdownPort, final String shutdownKey) {
        return new MonitorRunner(runner, new ShutdownMonitor(shutdownPort, shutdownKey, new ShutdownListener() {
            @Override
            public void onShutdown() {
                runner.stop();
            }
        }));
    }

    public FileMonitor createConfigurationMonitor(final File configuration, final FileRunner fileRunner) {
        return new FileMonitor(configuration, createListener(fileRunner));
    }

    public Monitor createSettingMonitor(final File settingsFile, final Iterable<File> configurationFiles, final FileRunner fileRunner) {
        List<File> files = newArrayList(settingsFile);
        Iterables.addAll(files, configurationFiles);
        return new FilesMonitor(files, createListener(fileRunner));
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
