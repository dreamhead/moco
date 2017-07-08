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

public class Java7Watcher implements Watcher {
    private static Logger logger = LoggerFactory.getLogger(Java7Watcher.class);
    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    private final WatchService service;
    private final Function<File, Void> listener;
    private boolean running;
    private final Multimap<WatchKey, Path> keys = HashMultimap.create();

    public Java7Watcher(final Function<File, Void> listener, final File[] files) {
        this.listener = listener;
        this.running = false;

        try {
            this.service = FileSystems.getDefault().newWatchService();
            for (File file : files) {
                Path directory = Files.directoryOf(file).toPath();
                WatchKey key = directory.register(service, new WatchEvent.Kind[]{ENTRY_MODIFY}, HIGH);
                keys.put(key, file.toPath());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void start() {
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

            final Collection<Path> paths = keys.get(key);
            for (WatchEvent<?> event : key.pollEvents()) {
                final Path context = (Path) event.context();
                if (event.kind().equals(ENTRY_MODIFY)) {
                    for (Path path : paths) {
                        if (path.endsWith(context)) {
                            listener.apply(context.toFile());
                            break;
                        }
                    }
                }
            }
            key.reset();
        } catch (ClosedWatchServiceException ignored) {
        } catch (InterruptedException e) {
            logger.error("Error happens", e);
        } finally {
            try {
                service.close();
            } catch (IOException ignore) {
            }
        }
    }

    @Override
    public void stop() {
        if (running) {
            this.running = false;
        }
    }
}
