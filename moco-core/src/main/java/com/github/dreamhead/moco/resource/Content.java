package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.HttpRequest;
import com.google.common.net.MediaType;

public interface Content {
    MediaType getContentType(HttpRequest request);
}
