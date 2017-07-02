package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.runner.FileRunner;
import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.github.dreamhead.moco.runner.watcher.Watchers.threadSafe;
import static com.google.common.collect.FluentIterable.from;

public abstract class AbstractWatcherFactory implements FileWatcherFactory {
    private static Logger logger = LoggerFactory.getLogger(AbstractWatcherFactory.class);

    protected abstract Watcher doCreate(final File file, final Function<File, Void> listener);

    public Watcher createWatcher(final FileRunner fileRunner, final File... files) {
        if (files.length == 0) {
            throw new IllegalArgumentException("No file is specified");
        }

        if (files.length == 1) {
            return create(fileRunner, files[0]);
        }

        return doCreate(fileRunner, files);
    }

    private Function<File, Void> listener(final FileRunner fileRunner) {
        return new Function<File, Void>() {
            @Override
            public Void apply(final File file) {
                logger.info("{} change detected.", file.getName());
                try {
                    fileRunner.restart();
                } catch (Exception e) {
                    logger.error("Fail to load configuration in {}.", file.getName());
                    logger.error(e.getMessage());
                }

                return null;
            }
        };
    }

    private Watcher create(final FileRunner fileRunner, final File file) {
        Function<File, Void> listener = listener(fileRunner);
        return threadSafe(doCreate(file, listener));
    }

    private Watcher doCreate(final FileRunner fileRunner, File[] files) {
        return new CompositeWatcher(from(files).transform(new Function<File, Watcher>() {
            @Override
            public Watcher apply(final File file) {
                return create(fileRunner, file);
            }
        }));
    }
}
