package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.runner.FileRunner;
import com.google.common.base.Function;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.google.common.collect.FluentIterable.from;

public class CommonsIoWatcherFactory implements WatcherFactory {
    private static Logger logger = LoggerFactory.getLogger(CommonsIoWatcherFactory.class);`

    @Override
    public MocoRunnerWatcher createWatcher(final FileRunner fileRunner, final File... files) {
        if (files.length == 0) {
            throw new IllegalArgumentException("No file is specified");
        }

        if (files.length == 1) {
            return new FileMocoRunnerWatcher(files[0], createListener(fileRunner));
        }

        return createFilesWatcher(files, createListener(fileRunner));
    }

    private MocoRunnerWatcher createFilesWatcher(final File[] files, final FileAlterationListener listener) {
        return new FilesMocoRunnerWatcher(from(files).transform(new Function<File, FileMocoRunnerWatcher>() {
            @Override
            public FileMocoRunnerWatcher apply(final File file) {
                return new FileMocoRunnerWatcher(file, listener);
            }
        }));
    }

    private FileAlterationListenerAdaptor createListener(final FileRunner fileRunner) {
        return new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(final File file) {
                logger.info("{} change detected.", file.getName());
                try {
                    fileRunner.restart();
                } catch (Exception e) {
                    logger.error("Fail to load configuration in {}.", file.getName());
                    logger.error(e.getMessage());
                }
            }
        };
    }
}
