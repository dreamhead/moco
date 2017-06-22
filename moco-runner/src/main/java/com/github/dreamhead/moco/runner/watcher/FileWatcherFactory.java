package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.runner.FileRunner;

import java.io.File;

public interface FileWatcherFactory {
    Watcher createWatcher(final FileRunner fileRunner, final File... files);
}
