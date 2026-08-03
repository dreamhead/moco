package com.github.dreamhead.moco.runner.watcher;

import com.github.dreamhead.moco.MocoException;
import com.github.dreamhead.moco.util.Files;
import com.github.dreamhead.moco.util.MocoExecutors;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.dreamhead.moco.util.Idles.idle;
import static com.sun.nio.file.SensitivityWatchEventModifier.HIGH;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public final class WatcherService {
    private static Logger logger = LoggerFactory.getLogger(WatcherService.class);
    private static final long REGISTER_INTERVAL = 1000;

    private ExecutorService executor = MocoExecutors.executor();
    private WatchService service;
    private boolean running;
    private Map<WatchKey, Set<Path>> keys = new HashMap<>();
    private Map<Path, Set<Function<File, Void>>> listeners = new HashMap<>();
    private Map<Path, Set<Path>> directoryToFiles = new HashMap<>();
    private Map<Path, WatchKey> directoryToKey = new HashMap<>();
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
        result = executor.submit(() -> {
            while (running) {
                loop();
            }

            doStop();
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
            Collection<Path> paths = keys.getOrDefault(key, Set.of());

            List<WatchEvent<?>> events = key.pollEvents().stream()
                    .filter(e -> e.kind().equals(ENTRY_MODIFY))
                    .collect(Collectors.toList());
            for (WatchEvent<?> event : events) {
                final Path context = (Path) event.context();
                List<Path> contextPaths = paths.stream()
                        .filter(p -> p.endsWith(context))
                        .collect(Collectors.toList());
                for (Path path : contextPaths) {
                    for (Function<File, Void> listener : this.listeners.getOrDefault(path, Set.of())) {
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
        keys.computeIfAbsent(key, k -> new HashSet<>()).add(path);
        listeners.computeIfAbsent(path, k -> new HashSet<>()).add(listener);
        directoryToFiles.computeIfAbsent(directory, k -> new HashSet<>()).add(path);

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
        if (!directoryToFiles.getOrDefault(directory, Set.of()).contains(path)) {
            return;
        }

        Set<Path> directoryFiles = directoryToFiles.get(directory);
        directoryFiles.remove(path);
        // Guava dropped the key once its last value went, and both the containsKey check
        // below and the isEmpty() check at the end rely on that.
        if (directoryFiles.isEmpty()) {
            directoryToFiles.remove(directory);
        }

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
