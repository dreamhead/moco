package com.github.dreamhead.moco.runner.watcher;

import com.google.common.base.Function;

import java.io.File;

public class Java7WatcherFactory implements FileWatcherFactory {
    @Override
    public Watcher createWatcher(final Function<File, Void> listener, final File... files) {
        if (files.length == 0) {
            throw new IllegalArgumentException("No file is specified");
        }
        return new Java7Watcher(listener, files);
    }
}
