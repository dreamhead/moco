package com.github.dreamhead.moco.runner;

import com.github.dreamhead.moco.bootstrap.StartArgs;
import com.github.dreamhead.moco.runner.monitor.ShutdownListener;
import com.github.dreamhead.moco.runner.monitor.ShutdownMonitor;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.google.common.collect.ImmutableList.of;

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
        final Runner dynamicRunner = new DynamicRunner(startArgs.getConfigurationFile(), startArgs.getPort());
        return createShutdownMonitor(dynamicRunner, startArgs.getShutdownPort(defaultShutdownPort), defaultShutdownKey);
    }
}
