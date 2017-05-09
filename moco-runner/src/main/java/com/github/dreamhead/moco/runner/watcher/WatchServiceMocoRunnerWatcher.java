package com.github.dreamhead.moco.runner.watcher;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;
import java.util.List;

import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class WatchServiceMocoRunnerWatcher implements MocoRunnerWatcher {

    private static final Logger logger = LoggerFactory.getLogger(WatchServiceMocoRunnerWatcher.class);

    private final List<File> files;
    private final Function<File, Void> listener;
    private final Multimap<WatchKey, Path> keys = HashMultimap.create();
    private boolean running = false;
    private WatchService watcher;

    WatchServiceMocoRunnerWatcher(List<File> files, Function<File, Void> listener) {
        this.files = files;
        this.listener = listener;
    }

    @Override
    public synchronized void startMonitor() {
        if (running) {
            throw new IllegalStateException();
        }

        try {
            watcher = FileSystems.getDefault().newWatchService();
            for (File file : files) {
                // the reason use HIGH: http://stackoverflow.com/questions/9588737/is-java-7-watchservice-slow-for-anyone-else
                final WatchKey key = directoryOf(file).register(watcher, new WatchEvent.Kind[]{ENTRY_MODIFY}, HIGH);
                keys.put(key, file.toPath());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        new Thread(createRunnable()).start();
        running = true;
    }

    @Override
    public synchronized void stopMonitor() {
        if (running) {
            try {
                watcher.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            keys.clear();
            running = false;
        }
    }

    private Runnable createRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                while (running) {
                    loop();
                }
            }
        };
    }

    private void loop() {
        final WatchKey key;
        try {
            key = watcher.take();
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return;
        }

        final Collection<Path> paths = keys.get(key);
        for (WatchEvent<?> event : key.pollEvents()) {
            final Path context = (Path) event.context();
            for (Path path : paths) {
                if (path.endsWith(context)) {
                    listener.apply(path.toFile());
                    break;
                }
            }
        }
        key.reset();
    }

    private Path directoryOf(File file) {
        final Path parent = file.toPath().getParent();
        return parent == null ? Paths.get(".") : parent;
    }
}
