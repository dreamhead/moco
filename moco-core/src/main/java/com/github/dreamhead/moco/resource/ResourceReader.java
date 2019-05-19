package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.model.MessageContent;

public interface ResourceReader {
    MessageContent readFor(Request request);
}
