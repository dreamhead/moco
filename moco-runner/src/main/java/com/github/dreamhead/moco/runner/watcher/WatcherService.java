package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.util.Files;
import com.github.dreamhead.moco.util.MocoExecutors;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.github.dreamhead.moco.util.Idles.idle;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Maps.newHashMap;
import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public final class WatcherService {
    private static Logger logger = LoggerFactory.getLogger(WatcherService.class);
    private static final long REGISTER_INTERVAL = 1000;

    private ExecutorService executor = MocoExecutors.executor();
    private WatchService service;
    private boolean running;
    private Multimap<WatchKey, Path> keys = HashMultimap.create();
    private Multimap<Path, Function<File, Void>> listeners = HashMultimap.create();
    private Multimap<Path, Path> directoryToFiles = HashMultimap.create();
    private Map<Path, WatchKey> directoryToKey = newHashMap();
    private Future<?> result;

    public synchronized void start() throws IOException {
        if (running) {
            return;
        }

        doStart();
    }

    private void doStart() throws IOException {
        this.service = FileSystems.getDefault().newWatchService();
        this.running = true;
        result = executor.submit(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    loop();
                }

                doStop();
            }
        });
    }

    private void doStop() {
        this.listeners.clear();
        this.keys.clear();
        this.directoryToFiles.clear();
        this.directoryToKey.clear();
    }

    private void loop() {
        try {
            WatchKey key = service.take();
            Collection<Path> paths = keys.get(key);

            for (WatchEvent<?> event : from(key.pollEvents()).filter(isModifyEvent())) {
                final Path context = (Path) event.context();
                for (Path path : from(paths).filter(isForPath(context))) {
                    for (Function<File, Void> listener : this.listeners.get(path)) {
                        listener.apply(path.toFile());
                    }
                    break;
                }
            }
            key.reset();
        } catch (ClosedWatchServiceException ignored) {
        } catch (InterruptedException e) {
            logger.error("Error happens", e);
        }
    }

    private Predicate<Path> isForPath(final Path context) {
        return new Predicate<Path>() {
            @Override
            public boolean apply(final Path path) {
                return path.endsWith(context);
            }
        };
    }

    private Predicate<WatchEvent<?>> isModifyEvent() {
        return new Predicate<WatchEvent<?>>() {
            @Override
            public boolean apply(final WatchEvent<?> event) {
                return event.kind().equals(ENTRY_MODIFY);
            }
        };
    }

    public synchronized void stop() {
        if (this.running) {
            try {
                this.running = false;
                this.service.close();
                this.result.get();
            } catch (Exception e) {
                throw new MocoException(e);
            }
        }
    }

    public void register(final File file, final Function<File, Void> listener) {
        Path directory = Files.directoryOf(file).toPath();
        WatchKey key = registerDirectory(directory);
        Path path = file.toPath();
        keys.put(key, path);
        listeners.put(path, listener);
        directoryToFiles.put(directory, path);

        idle(REGISTER_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private WatchKey registerDirectory(final Path directory) {
        if (directoryToKey.containsKey(directory)) {
            return directoryToKey.get(directory);
        }

        try {
            WatchKey key = directory.register(service, new WatchEvent.Kind[]{ENTRY_MODIFY}, HIGH);
            directoryToKey.put(directory, key);
            return key;
        } catch (IOException e) {
            throw new MocoException(e);
        }
    }

    public void unregister(final File file) {
        Path directory = Files.directoryOf(file).toPath();
        Path path = file.toPath();
        if (!directoryToFiles.containsEntry(directory, path)) {
            return;
        }

        directoryToFiles.remove(directory, path);

        if (!directoryToFiles.containsKey(directory)) {
            WatchKey key = directoryToKey.remove(directory);
            if (key != null) {
                key.cancel();
            }
        }

        if (directoryToFiles.isEmpty()) {
            this.stop();
        }
    }
}
