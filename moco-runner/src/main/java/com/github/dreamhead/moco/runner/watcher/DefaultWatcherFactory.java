package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.MocoException;
import com.google.common.base.Function;

import java.io.File;
import java.io.IOException;

import static com.google.common.collect.FluentIterable.from;

public final class DefaultWatcherFactory implements FileWatcherFactory {
    private WatcherService service = new WatcherService();

    @Override
    public Watcher createWatcher(final Function<File, Void> listener, final File... files) {
        if (files.length == 0) {
            throw new IllegalArgumentException("No file is specified");
        }

        try {
            this.service.start();
        } catch (IOException e) {
            throw new MocoException(e);
        }

        return new CompositeWatcher(from(files).transform(new Function<File, Watcher>() {
            @Override
            public Watcher apply(final File file) {
                return new DefaultWatcher(service, listener, file);
            }
        }));
    }
}
