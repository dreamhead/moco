package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.MocoException;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Java7Watcher implements Watcher {
    private static Logger logger = LoggerFactory.getLogger(Java7Watcher.class);
    private static WatcherService service = new WatcherService();
    private final Function<File, Void> listener;
    private final File file;

    public Java7Watcher(final Function<File, Void> listener, final File file) {
        this.listener = listener;
        this.file = file;
    }

    @Override
    public synchronized void start() {
        try {
            if (!service.isRunning()) {
                service.start();
            }

            service.register(file, listener);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    @Override
    public void stop() {
    }
}
