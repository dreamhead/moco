package com.github.dreamhead.moco.runner.watcher;

import com.google.common.base.Function;
import org.apache.commons.io.monitor.FileAlterationListener;

import java.io.File;

import static com.google.common.collect.FluentIterable.from;

public class FilesMocoRunnerWatcher implements MocoRunnerWatcher {
    private final Iterable<FileMocoRunnerWatcher> monitors;

    public FilesMocoRunnerWatcher(final Iterable<FileMocoRunnerWatcher> monitors) {
        this.monitors = monitors;
    }

    @Override
    public void startMonitor() {
        for (FileMocoRunnerWatcher monitor : monitors) {
            monitor.startMonitor();
        }
    }

    @Override
    public void stopMonitor() {
        for (FileMocoRunnerWatcher monitor : monitors) {
            monitor.stopMonitor();
        }
    }

}
