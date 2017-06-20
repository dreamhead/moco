package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.runner.FileRunner;
import com.google.common.base.Function;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;

import static com.github.dreamhead.moco.runner.watcher.ThreadSafeRunnerWatcher.INTERVAL;
import static com.google.common.collect.FluentIterable.from;

public class CommonsIoWatcherFactory implements WatcherFactory {
    private static Logger logger = LoggerFactory.getLogger(CommonsIoWatcherFactory.class);

    @Override
    public MocoRunnerWatcher createWatcher(final FileRunner fileRunner, final File... files) {
        if (files.length == 0) {
            throw new IllegalArgumentException("No file is specified");
        }

        FileAlterationListener listener = createListener(fileRunner);
        if (files.length == 1) {
            File file = files[0];
            return createWatcher(file, listener);
        }

        return createFilesWatcher(files, listener);
    }

    private MocoRunnerWatcher createWatcher(final File file, final FileAlterationListener listener) {
        return new ThreadSafeRunnerWatcher(new CommonsIoWatcher(monitorFile(file, listener)));
    }

    private MocoRunnerWatcher createFilesWatcher(final File[] files, final FileAlterationListener listener) {
        return new CompositeRunnerWatcher(from(files).transform(new Function<File, MocoRunnerWatcher>() {
            @Override
            public MocoRunnerWatcher apply(final File file) {
                return createWatcher(file, listener);
            }
        }));
    }

    private FileAlterationListener createListener(final FileRunner fileRunner) {
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

    private FileAlterationMonitor monitorFile(final File file, final FileAlterationListener listener) {
        File parentFile = file.getParentFile();
        File directory = toDirectory(parentFile);
        FileAlterationObserver observer = new FileAlterationObserver(directory, sameFile(file));
        observer.addListener(listener);

        return new FileAlterationMonitor(INTERVAL, observer);
    }

    private File toDirectory(final File parentFile) {
        if (parentFile == null) {
            return new File(".");
        }

        return parentFile;
    }

    private FileFilter sameFile(final File file) {
        return new FileFilter() {
            @Override
            public boolean accept(final File detectedFile) {
                return file.getName().equals(detectedFile.getName());
            }
        };
    }
}
