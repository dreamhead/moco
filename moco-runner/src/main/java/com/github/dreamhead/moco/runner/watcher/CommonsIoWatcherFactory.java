package com.github.dreamhead.moco.runner.watcher;

import com.google.common.base.Function;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.util.Files.directoryOf;

public class CommonsIoWatcherFactory extends AbstractWatcherFactory {
    private static final long INTERVAL = TimeUnit.SECONDS.toMillis(1);

    protected Watcher doCreate(final File file, final Function<File, Void> listener) {
        return new CommonsIoWatcher(monitorFile(file, createListener(listener)));
    }

    private FileAlterationListener createListener(final Function<File, Void> listener) {
        return new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(final File file) {
                listener.apply(file);
            }
        };
    }

    private FileAlterationMonitor monitorFile(final File file, final FileAlterationListener listener) {
        File directory = directoryOf(file);
        FileAlterationObserver observer = new FileAlterationObserver(directory, sameFile(file));
        observer.addListener(listener);

        return new FileAlterationMonitor(INTERVAL, observer);
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
