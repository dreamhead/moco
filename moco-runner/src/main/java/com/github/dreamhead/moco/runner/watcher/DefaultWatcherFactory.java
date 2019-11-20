package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.MocoException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        return new CompositeWatcher(Arrays.stream(files)
                .map((file -> new DefaultWatcher(service, listener, file)))
                .collect(Collectors.toList()));
    }
}
