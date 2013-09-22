package com.github.dreamhead.moco.runner.monitor;

import com.google.common.base.Function;
import org.apache.commons.io.monitor.FileAlterationListener;

import java.io.File;

import static com.google.common.collect.FluentIterable.from;

public class FilesMocoRunnerMonitor implements MocoRunnerMonitor {
    private final Iterable<FileMocoRunnerMonitor> monitors;

    public FilesMocoRunnerMonitor(Iterable<File> files, final FileAlterationListener listener) {
        this.monitors = from(files).transform(new Function<File, FileMocoRunnerMonitor>() {
            @Override
            public FileMocoRunnerMonitor apply(File file) {
                return new FileMocoRunnerMonitor(file, listener);
            }
        });
    }

    @Override
    public void startMonitor() {
        for (FileMocoRunnerMonitor monitor : monitors) {
            monitor.startMonitor();
        }
    }

    @Override
    public void stopMonitor() {
        for (FileMocoRunnerMonitor monitor : monitors) {
            monitor.stopMonitor();
        }
    }
}
