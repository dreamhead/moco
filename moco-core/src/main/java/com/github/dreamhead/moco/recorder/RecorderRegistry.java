package com.github.dreamhead.moco.recorder;

import java.util.HashMap;
import java.util.Map;

public class RecorderRegistry {
    private Map<String, RequestRecorder> recorders = new HashMap<>();

    public RequestRecorder recorderOf(final String name) {
        RequestRecorder recorder = recorders.get(name);

        if (recorder != null) {
            return recorder;
        }

        return recorders.computeIfAbsent(name, s -> new InMemoryRequestRecorder());
    }

    private static RecorderRegistry REGISTRY = new RecorderRegistry();

    public static RecorderRegistry defaultRegistry() {
        return REGISTRY;
    }

    private static Map<String, RecorderRegistry> registries;

    public static RecorderRegistry registryOf(final String name) {
        if (registries == null) {
            registries = new HashMap<>();
        }

        return registries.computeIfAbsent(name, s -> new RecorderRegistry());
    }
}
