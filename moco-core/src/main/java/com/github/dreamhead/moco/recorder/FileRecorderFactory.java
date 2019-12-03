package com.github.dreamhead.moco.recorder;

public class FileRecorderFactory implements RecorderFactory {
    private RecorderTape tape;

    public FileRecorderFactory(final RecorderTape tape) {
        this.tape = tape;
    }

    @Override
    public RequestRecorder newRecorder() {
        return new FileRequestRecorder(tape);
    }
}
