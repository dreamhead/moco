package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.runner.FileRunner;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;

import static com.github.dreamhead.moco.runner.watcher.Watchers.INTERVAL;

public class CommonsIoWatcherFactory extends AbstractWatcherFactory {
    private static Logger logger = LoggerFactory.getLogger(CommonsIoWatcherFactory.class);

    protected Watcher doCreate(final FileRunner fileRunner, final File file) {
        return new CommonsIoWatcher(monitorFile(file, createListener(fileRunner)));
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
