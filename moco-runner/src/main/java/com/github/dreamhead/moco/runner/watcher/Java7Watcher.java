package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.MocoException;
import com.google.common.base.Function;

import java.io.File;
import java.io.IOException;

public class Java7Watcher implements Watcher {
    private final WatcherService service;
    private final Function<File, Void> listener;
    private final File file;

    public Java7Watcher(WatcherService service, final Function<File, Void> listener, final File file) {
        this.service = service;
        this.listener = listener;
        this.file = file;
    }

    @Override
    public synchronized void start() {
        try {
            service.register(file, listener);
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    @Override
    public void stop() {
    }
}
