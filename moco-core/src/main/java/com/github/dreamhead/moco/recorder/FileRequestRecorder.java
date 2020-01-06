package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;

public class FileRequestRecorder implements RequestRecorder {
    private String name;
    private RecorderTape tape;

    public FileRequestRecorder(final String name, final RecorderTape tape) {
        this.name = name;
        this.tape = tape;
    }

    @Override
    public final void record(final HttpRequest httpRequest) {
        tape.write(name, httpRequest);
    }

    @Override
    public final HttpRequest getRequest() {
        return tape.read(name);
    }
}
