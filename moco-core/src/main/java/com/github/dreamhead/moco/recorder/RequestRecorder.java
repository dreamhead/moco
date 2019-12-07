package com.github.dreamhead.moco.recorder;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.MessageContent;
import com.google.common.net.MediaType;

public interface RequestRecorder {
    void record(HttpRequest httpRequest);

    HttpRequest getRequest();

    MessageContent getContent();

    MediaType getContentType();
}
