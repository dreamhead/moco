package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.runner.monitor.*;
import com.google.common.collect.Iterables;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class RunnerFactory {
    private static Logger logger = LoggerFactory.getLogger(RunnerFactory.class);

    private final int defaultShutdownPort;
    private final String defaultShutdownKey;

    public RunnerFactory(int defaultShutdownPort, String defaultShutdownKey) {
        this.defaultShutdownPort = defaultShutdownPort;
        this.defaultShutdownKey = defaultShutdownKey;
    }

    public Runner createShutdownMonitor(final Runner runner, final int shutdownPort, final String shutdownKey) {
        return new MonitorRunner(runner, new ShutdownMonitor(shutdownPort, shutdownKey, new ShutdownListener() {
            @Override
            public void onShutdown() {
                runner.stop();
            }
        }));
    }

    public Runner createRunner(StartArgs startArgs) {
        Runner dynamicRunner = createDynamicRunner(startArgs);
        return createShutdownMonitor(dynamicRunner, startArgs.getShutdownPort(defaultShutdownPort), defaultShutdownKey);
    }

    private Runner createDynamicRunner(StartArgs startArgs) {
        if (startArgs.hasConfigurationFile()) {
            return createDynamicConfigurationRunner(startArgs);
        }

        return createDynamicSettingRunner(startArgs);
    }

    private Runner createDynamicSettingRunner(StartArgs startArgs) {
        File settingsFile = new File(startArgs.getSettings());
        List<File> files = newArrayList(settingsFile);
        FileRunner wrapper = FileRunner.createSettingFileRunner(settingsFile, startArgs.getPort());
        Runner runner = wrapper.getRunner();
        Monitor fileMonitor = createSettingMonitor(files, wrapper, (SettingRunner) runner);
        return new MonitorRunner(wrapper, fileMonitor);
    }

    private Monitor createSettingMonitor(List<File> files, FileRunner wrapper, SettingRunner runner) {
        Iterables.addAll(files, runner.getFiles());
        return new FilesMonitor(files, createListener(wrapper));
    }

    private Runner createDynamicConfigurationRunner(StartArgs startArgs) {
        final File configuration = new File(startArgs.getConfigurationFile());
        final FileRunner fileRunner = FileRunner.createConfigurationFileRunner(configuration, startArgs.getPort());
        Monitor fileMonitor = new FileMonitor(configuration, createListener(fileRunner));
        return new MonitorRunner(fileRunner, fileMonitor);
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
