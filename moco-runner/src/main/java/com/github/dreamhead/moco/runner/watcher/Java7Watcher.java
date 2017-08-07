package com.github.dreamhead.moco.runner.watcher;

import com.google.common.base.Function;

import java.io.File;

public class Java7Watcher implements Watcher {
    private final WatcherService service;
    private final Function<File, Void> listener;
    private final File file;

    public Java7Watcher(final WatcherService service, final Function<File, Void> listener, final File file) {
        this.service = service;
        this.listener = listener;
        this.file = file;
    }

    @Override
    public void start() {
        service.register(file, listener);
    }

    @Override
    public void stop() {
        service.unregister(file);
    }
}
