package com.github.dreamhead.moco.recorder;

import java.util.HashMap;
import java.util.Map;

public class RecorderRegistry {
    private Map<String, RequestRecorder> recorders;

    public RequestRecorder of(final String name) {
        if (recorders == null) {
            recorders = new HashMap<>();
        }

        RequestRecorder recorder = recorders.get(name);

        if (recorder != null) {
            return recorder;
        }

        RequestRecorder newRecorder = new RequestRecorder();
        recorders.put(name, newRecorder);
        return newRecorder;
    }

    private static RecorderRegistry REGISTRY = new RecorderRegistry();

    public static RecorderRegistry defaultRegistry() {
        return REGISTRY;
    }
}
