package com.github.dreamhead.moco.runner.watcher;

import com.google.common.base.Function;

import java.io.File;

public interface FileWatcherFactory {
    Watcher createWatcher(Function<File, Void> listener, File... files);
}
