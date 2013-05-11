package com.github.dreamhead.moco.runner.monitor;

import com.google.common.base.Function;
import org.apache.commons.io.monitor.FileAlterationListener;

import java.io.File;

import static com.google.common.collect.FluentIterable.from;

public class FilesMonitor implements Monitor {
    private Iterable<FileMonitor> monitors;

    public FilesMonitor(Iterable<File> files, final FileAlterationListener listener) {
        this.monitors = from(files).transform(new Function<File, FileMonitor>() {
            @Override
            public FileMonitor apply(File file) {
                return new FileMonitor(file, listener);
            }
        });
    }

    @Override
    public void startMonitor() {
        for (FileMonitor monitor : monitors) {
            monitor.startMonitor();
        }
    }

    @Override
    public void stopMonitor() {
        for (FileMonitor monitor : monitors) {
            monitor.stopMonitor();
        }
    }
}
