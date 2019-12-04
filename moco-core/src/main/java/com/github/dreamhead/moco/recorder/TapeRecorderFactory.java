package com.github.dreamhead.moco.recorder;

public class TapeRecorderFactory implements RecorderFactory {
    private RecorderTape tape;

    public TapeRecorderFactory(final RecorderTape tape) {
        this.tape = tape;
    }

    @Override
    public RequestRecorder newRecorder(String name) {
        return new FileRequestRecorder(name, tape);
    }
}
