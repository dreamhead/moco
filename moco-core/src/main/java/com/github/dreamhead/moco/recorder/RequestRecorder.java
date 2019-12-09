package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;

public interface RequestRecorder {
    void record(HttpRequest httpRequest);

    HttpRequest getRequest();
}
