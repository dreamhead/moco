package com.github.dreamhead.moco.runner.monitor;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;

public class FileMocoRunnerMonitor implements MocoRunnerMonitor {
    public static final int INTERVAL = 1000;
    private static Logger logger = LoggerFactory.getLogger(FileMocoRunnerMonitor.class);

    private final FileAlterationMonitor monitor;
    private boolean running = false;

    public FileMocoRunnerMonitor(File file, FileAlterationListener listener) {
        this.monitor = monitorFile(file, listener);
    }

    public synchronized void startMonitor() {
        try {
            monitor.start();
            running = true;
        } catch (Exception e) {
            logger.error("Error found.", e);
        }
    }

    public synchronized void stopMonitor() {
        try {
            if (monitor != null && running) {
                monitor.stop();
                running = false;
            }
        } catch (Exception e) {
            logger.error("Error found.", e);
        }
    }

    private FileAlterationMonitor monitorFile(File file, FileAlterationListener listener) {
        File parentFile = file.getParentFile();
        File directory = (parentFile == null) ? new File(".") : parentFile;
        FileAlterationObserver observer = new FileAlterationObserver(directory, sameFile(file));
        observer.addListener(listener);

        return new FileAlterationMonitor(INTERVAL, observer);
    }

    private FileFilter sameFile(final File file) {
        return new FileFilter() {
            @Override
            public boolean accept(File detectedFile) {
                return file.getName().equals(detectedFile.getName());
            }
        };
    }
}
