package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.util.Files;
import com.google.common.base.Function;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class WatcherService {
    private static Logger logger = LoggerFactory.getLogger(WatcherService.class);

    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private WatchService service;
    private boolean running;
    private final Multimap<WatchKey, Path> keys = HashMultimap.create();
    private final Multimap<Path, Function<File, Void>> listeners = HashMultimap.create();

    public boolean isRunning() {
        return this.running;
    }

    public void start() throws IOException {
        if (running) {
            throw new IllegalStateException();
        }

        this.service = FileSystems.getDefault().newWatchService();
        this.running = true;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    loop();
                }
            }
        });
    }

    private void loop() {
        try {
            WatchKey key = service.take();
            Collection<Path> paths = keys.get(key);
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind().equals(ENTRY_MODIFY)) {
                    final Path context = (Path) event.context();
                    for (Path path : paths) {
                        if (path.endsWith(context)) {
                            for (Function<File, Void> listener : this.listeners.get(path)) {
                                listener.apply(path.toFile());
                            }
                            break;
                        }
                    }
                }
            }
            key.reset();
        } catch (ClosedWatchServiceException ignored) {
        } catch (InterruptedException e) {
            logger.error("Error happens", e);
        }
    }

    public void stop() {
        if (this.running) {
            this.running = false;
        }
    }

    public void register(final File file, final Function<File, Void> listener) throws IOException {
        Path directory = Files.directoryOf(file).toPath();
        WatchKey key = directory.register(service, new WatchEvent.Kind[]{ENTRY_MODIFY}, HIGH);
        Path path = file.toPath();
        keys.put(key, path);
        listeners.put(path, listener);
    }
}
