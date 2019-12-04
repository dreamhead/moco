package com.github.dreamhead.moco.recorder;

public interface RecorderFactory {
    RequestRecorder newRecorder(String name);

    RecorderFactory IN_MEMORY = (String name) -> new InMemoryRequestRecorder();
}
