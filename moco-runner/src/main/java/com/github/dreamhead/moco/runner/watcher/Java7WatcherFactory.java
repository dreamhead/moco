package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.MocoException;
import com.google.common.base.Function;

import java.io.File;
import java.io.IOException;

import static com.google.common.collect.FluentIterable.from;

public class Java7WatcherFactory implements FileWatcherFactory {
    private WatcherService service = new WatcherService();

    public Java7WatcherFactory() {
        try {
            this.service.start();
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    @Override
    public Watcher createWatcher(final Function<File, Void> listener, final File... files) {
        if (files.length == 0) {
            throw new IllegalArgumentException("No file is specified");
        }

        return new CompositeWatcher(from(files).transform(new Function<File, Watcher>() {
            @Override
            public Watcher apply(final File file) {
                return new Java7Watcher(service, listener, file);
            }
        }));
    }
}
