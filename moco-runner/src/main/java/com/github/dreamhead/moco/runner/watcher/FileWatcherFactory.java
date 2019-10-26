package com.github.dreamhead.moco.runner.watcher;

import java.io.File;
import java.util.function.Function;

public interface FileWatcherFactory {
    Watcher createWatcher(Function<File, Void> listener, File... files);
}
