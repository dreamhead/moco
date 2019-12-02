package com.github.dreamhead.moco.recorder;

public interface RecorderFactory {
    RequestRecorder newRecorder();

    RecorderFactory IN_MEMORY = InMemoryRequestRecorder::new;
}
