package com.github.dreamhead.moco.runner.watcher;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonsIoWatcher implements Watcher {
    private static Logger logger = LoggerFactory.getLogger(CommonsIoWatcher.class);

    private final FileAlterationMonitor monitor;

    public CommonsIoWatcher(final FileAlterationMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void start() {
        try {
            monitor.start();
        } catch (Exception e) {
            logger.error("Error found.", e);
        }
    }

    @Override
    public void stop() {
        try {
            monitor.stop();
        } catch (Exception e) {
            logger.error("Error found.", e);
        }
    }
}
